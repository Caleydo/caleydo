#! /usr/bin/env python
#
#-------------------------------------------------------------------------------
#
# Export Layers - Python GIMP plug-in
#
# Copyright (C) 2013 khalim19
# Copyright (C) 2014 Thomas Geymayer
# 
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#
#-------------------------------------------------------------------------------

"""
================================
Export Layers
================================

This is a GIMP plug-in written in Python language that exports layers as separate
images in (almost) any file format supported by GIMP, possibly including plug-ins.

This plug-in requires at least GIMP version 2.8 to be installed.

Just drop into ~/.gimp-2.8/plug-ins/ (and set executable bit)

================
Summary
================
This plug-in:
* exports layers as separate images in almost any valid exportable file format
  pre-installed in GIMP
* uses native windows of the file format export procedures to adjust file format settings
* uses layer names as filenames for the exported images

================
Usage
================
From the main menu, go to "File -> Export Layers...". A dialog appears, allowing
you to specify the output directory, output file format for all layers and
several options adjusting the output.

To specify file format, type file extension corresponding to the file format
in the text field (with or without the leading dot). Almost any file format is
supported, including XCF format native to the GIMP. File formats provided by third
party plug-ins should theoretically work as well - at the very least,
GIMP DDS Plugin was tested and works correctly.

The file formats that do not work with this plug-in are OpenRaster (.ora)
and Colored XHTML (.xhtml) - see Known Issues below for more information.

To finally export layers, click the "Export Layers" button. For the first layer,
a window corresponding to the file format export appears, allowing you to adjust
format settings. Not all file formats have settings, so no window may appear
depending on the file format you specified.

For the subsequent layers, the file export procedure uses the values you
specified in the window for the first file. However, some file formats have to
display the window for each file - see Known Issues below for more information.

After all layers are exported and no error was encountered, the dialog is closed.

If you specified invalid file format, an error message appears, prompting you
to specify a valid file format. During the export, you might encounter other errors,
such as exporting to a directory with denied permission, or file export procedure
failing to export files for some reason, etc. In such case, the export is stopped,
but the dialog will still remain open. 

When files with the same name already exist, a dialog appears, prompting you to
choose one of the following actions: skip the new file, overwrite the existing
file, rename the new file or rename the existing file. You can also apply the
action to all files by checking "Apply action to all files".

The plug-in can also be run non-interactively, but the file export procedures
will not display the window and instead use their default (or last used) values.

================
Options
================

Ignore invisible layers:
If checked, invisible layers will not be exported. Visible layers within
invisible layer groups will also not be exported.

Autocrop layers:
If checked, layers will be autocropped before being exported.

Use image size instead of layer size:
If checked, layers will be resized to the image (canvas) size. If layers are
partially outside the image canvas, they will be cut off, thus if you want to
export the entire layer, leave this option unchecked.

================
Known Issues
================

The following file formats are not functioning properly with this plug-in:
* OpenRaster (.ora) - crashes GIMP,
* colored XHTML (.xhtml) - does not save images at all,
* KISS CEL (.cel) - throws error/warning messages, but saves images anyway.

The following file formats have to display the file format settings window
for each layer, not just the first layer:
* FLI (.fli, .flc),
* C source (.c),
* HTML (.html, .htm).

Only ASCII letters, digits and certain special characters are allowed in filenames.
Other characters are deleted (in filenames only, not in layer names).

Progress bar is currently not very useful, as it does not update properly
after each exported layer.
"""


"""
TODO:

* use custom progress bar
  - updates after successfully exported file
  - displays current filename text (NOT x out of y images, since that can be read
    from the progress bar itself)
  - requires two separate threads - one for the GUI (the main thread) and the other for the export_layers function itself


* consider renaming "Overwrite" to "Replace" to be consistent with the Export menu when overwriting
  - consequently, replace OverwriteMode.OVERWRITE with OverwriteMode.REPLACE
  - however, leave "OverwriteMode" intact

* beside using layer names as filenames, consider providing a way to adjust the names before export
  - e.g. by adding prefix, suffix, etc.
    - make sure the prefix/suffix is added after file extension is stripped if identical to export file format
  - alternatively, let the user define a filename pattern that overwrites layer names

* remember current directory (in GTK terminology, "current folder") for each opened image separately
  instead of using the same folder for all opened images (except if the plug-in hasn't been run at least once yet)

* the overwrite dialog should be hidden once the user selected option - however,
  the dialog is still displayed even if the file format dialog is displayed

* multilayer images and layer groups
  - make an option that exports layers from one layer group as one image (instead of separate images for each layer)
    - this requires that the file format support multiple layers - this needs to be taken care of

* output directory - in case the plug-in is invoked non-interactively, make sure the specified directory as string is valid
  - this includes the absence of invalid characters in a filename

* proper naming of layer groups
  - currently, if layer groups have identical names after processing, they
    are not made unique properly, causing the output images to be saved in wrong directories
      - will not happen as long as you use letters, digits,
        and most of the punctuation characters in the names of layer groups
  - for this to be done properly, a tree-like structure needs to be implemented and used
  - if layer group has no children, create directory anyway

* make plug-in "Unicode-friendly" 
  - allow Unicode characters in filenames when deleting disallowed characters
"""

#=============================================================================== 

import string

import os
import sys
from datetime import datetime
import abc

import pygtk
pygtk.require("2.0")
import gtk

import gimp
import gimpplugin
import gimpenums
import gimpui
from gimpshelf import shelf

#===============================================================================

pdb = gimp.pdb

#===============================================================================

PLUGIN_TITLE = "Export Layers"
SHELF_PREFIX = "export_layers_"

#===============================================================================
  
def uniquify_string(s, existing_strings):
  """
  If string "s" is in the existing_strings list, return a unique string.
  """
  if s in existing_strings:
    j = 1
    uniq_s = '{0} ({1})'.format(s, j)
    while uniq_s in existing_strings:
      j += 1
      uniq_s = '{0} ({1})'.format(s, j)
    return uniq_s
  else:
    return s

def uniquify_filename(filename):
  """
  If a file having filename "filename" exists, return a unique filename.
  """
  root, ext = os.path.splitext(filename)
  
  if os.path.exists(filename):
    i = 1
    uniq_filename = ''.join((root, " (", str(i), ")", ext))
    while os.path.exists(uniq_filename):
      i += 1
      uniq_filename = ''.join((root, " (", str(i), ")", ext))
    return uniq_filename
  else:
    return filename

class ExportLayersError(Exception):
  pass

class ExportLayersCancelError(ExportLayersError):
  pass

#===============================================================================

# Original version:
# http://mail.python.org/pipermail/python-list/2007-May/438106.html
# Author: Peter Otten

class Tee(object):
  
  def __init__(self, stream, filename, mode):
    self.stream_name = stream
    self.init(stream, filename, mode)
    
    self.first_time = True
    
  def __del__(self):
    self.reset()
  
  def init(self, stream, filename, mode):
    self.stream = getattr(sys, stream)
    setattr(sys, stream, self)
    
    self.file = open(filename, mode)
    
    self.first_time = True
  
  def reset(self):
    setattr(sys, self.stream_name, self.stream)
    self.file.close()
  
  def write(self, data):
    #XXX: Write current date time to file before writing the first output.
    #     This avoids logging the date time when the object is instantiated,
    #     but no actual output is written during the course of the program.
    if self.first_time:
      self.file.write(_get_formatted_date())
      self._write(data + '\n')
      self.first_time = False
    else:
      self.write = self._write
  
  def _write(self, data):
    self.file.write(data)
    self.stream.write(data)
#    self.file.flush()


def _get_formatted_date():
  return '\n'.join(('', '=' * 80, str(datetime.now()), '\n'))

# Log output for testing purposes. If something in the plug-in
# doesn't work or fails, uncomment the lines and examine the output of
# "export_layers.log" and "export_layers_error.log" files in the "plug-ins" directory.
#Tee('stdout', os.path.join(gimp.directory, 'plug-ins', 'export_layers.log'), 'a')
#Tee('stderr', os.path.join(gimp.directory, 'plug-ins', 'export_layers_error.log'), 'a')

#===============================================================================

class OverwriteMode(object):
  
  OVERWRITE_MODES = (SKIP, OVERWRITE, RENAME_NEW, RENAME_EXISTING) = (1, 2, 3, 4)
  
  __metaclass__ = abc.ABCMeta
  
  @abc.abstractmethod
  def get_overwrite(self, *args, **kwargs):
    """
    Return a value indicating how to handle conflicting files outside this class
    by letting the user choose the value.
    
    The actual implementation of handling conflicting files is left
    to the programmer using the return value provided by this method.
    """
    pass
  
class DialogOverwrite(OverwriteMode):
  
  """
  This class is used to display dialog prompt in an interactive environment
  when a file about to be saved has the same name as an already existing file.
  """
  
  def __init__(self, overwrite_val=None, apply_to_all=False, parent=None):
    self._overwrite_val = overwrite_val
    self._apply_to_all = apply_to_all
    
    
    self._dialog = gtk.Dialog(title="", parent=parent, flags=gtk.DIALOG_MODAL | gtk.DIALOG_DESTROY_WITH_PARENT)
    self._dialog.set_transient_for(parent)
    self._dialog.set_border_width(8)
    self._dialog.set_resizable(False)
    
    self._hbox_dialog_contents = gtk.HBox(homogeneous=False)
    self._hbox_dialog_contents.set_spacing(10)
    self._dialog_icon = gtk.Image()
    self._dialog_icon.set_from_stock(gtk.STOCK_DIALOG_QUESTION, gtk.ICON_SIZE_DIALOG)
    self._dialog_text = gtk.Label("")
    self._hbox_dialog_contents.pack_start(self._dialog_icon, expand=False, fill=False)
    self._hbox_dialog_contents.pack_start(self._dialog_text, expand=False, fill=False)
    
    self._hbox_apply_to_all = gtk.HBox(homogeneous=False)
    self._hbox_apply_to_all.set_spacing(5)
    self._apply_to_all_checkbox = gtk.CheckButton(label="Apply action to all files")
    self._hbox_apply_to_all.pack_start(self._apply_to_all_checkbox, expand=False, fill=False)
    
    self._dialog.vbox.set_spacing(3)
    self._dialog.vbox.pack_start(self._hbox_dialog_contents, expand=False, fill=False)
    self._dialog.vbox.pack_start(self._hbox_apply_to_all, expand=False, fill=False)
    
    self._button_skip = self._dialog.add_button("Skip", self.SKIP)
    self._button_overwrite = self._dialog.add_button("Overwrite", self.OVERWRITE)
    self._button_rename_new = self._dialog.add_button("Rename new file", self.RENAME_NEW)
    self._button_rename_existing = self._dialog.add_button("Rename existing file", self.RENAME_EXISTING)
    self._dialog.action_area.set_spacing(8)
    
    self._apply_to_all_checkbox.connect("toggled", self._set_apply_to_all)
    
    self._dialog.set_focus(self._button_overwrite)
  
  def _set_apply_to_all(self, widget):
    self._apply_to_all = self._apply_to_all_checkbox.get_active()
  
  def get_value(self):
    return self._overwrite_val
  
  def get_overwrite(self, filename=None):
    """
    Display dialog prompt where the user chooses overwrite mode.
    
    This method only returns a value indicating how to handle conflicting files.
    The actual implementation is left to the programmer using the return value
    provided by this method.
    """
    
    if self._overwrite_val is None or self._apply_to_all == False:
      if filename is not None:
        text_filename = "named \"" + filename + "\""
      else:
        text_filename = "with the same name"
      self._dialog_text.set_markup("<span font_size=\"large\"><b>A file " + text_filename +
                                   " already exists.\nWhat would you like to do?</b></span>")
      self._dialog.show_all()
      self._overwrite_val = self._dialog.run()
      if self._overwrite_val not in self.OVERWRITE_MODES:
        # Treat any other response (such as cancel operation) as Skip.
        self._overwrite_val = self.SKIP
      self._dialog.hide_all()
      
      return self._overwrite_val
    else:
      return self._overwrite_val

class NoninteractiveOverwrite(OverwriteMode):
  
  """
  This class simply stores overwrite mode specified upon the object
  instantiation. The object is suitable to use in a non-interactive environment,
  i.e. with no user interaction.
  """
  
  def __init__(self, overwrite_val=None):
    # Default value: OVERWRITE
    self._overwrite_val = overwrite_val if not None else self.OVERWRITE
  
  def get_overwrite(self, *args, **kwargs):
    return self._overwrite_val
  
  def get_value(self):
    return self._overwrite_val

#===============================================================================

def display_message(msg, msg_handler=gimpenums.MESSAGE_BOX):
  """
  Display message with specified handler using GIMP's gimp_message() procedure.
  """
  orig_msg_handler = pdb.gimp_message_get_handler()
  pdb.gimp_message_set_handler(msg_handler)
  pdb.gimp_message(msg)
  pdb.gimp_message_set_handler(orig_msg_handler)

# def get_image_path(image):
#   if image.uri is not None:
#     pass
#   else:
#     return gimp.directory

def _export_layer(run_mode, overwrite_mode, image, layer, filename):

  # Handle conflicting files.
  if os.path.exists(filename):
    overwrite = overwrite_mode.get_overwrite(filename = os.path.basename(filename))
    if overwrite == OverwriteMode.SKIP:
      return
    elif overwrite == OverwriteMode.OVERWRITE:
      # Nothing needs to be done here.
      pass
    elif overwrite in (OverwriteMode.RENAME_NEW, OverwriteMode.RENAME_EXISTING):
      uniq_filename = uniquify_filename(filename)
      if overwrite == OverwriteMode.RENAME_NEW:
        filename = uniq_filename
      else:
        os.rename(filename, uniq_filename)

  try:
    pdb.gimp_file_save( image, 
                        layer,
                        filename,
                        os.path.basename(filename) )
  except RuntimeError as e:
    # HACK: Since RuntimeError could indicate anything including pdb.gimp_file_save
    # failure, this is the only plausible way to intercept Cancel operation.
    if "cancelled" in e.message:
      pdb.gimp_image_delete(image)
      raise ExportLayersCancelError(e.message)
    else:
      # Next time, try forcing interactive mode if it was non-interactive
      # (certain file types do not allow non-interactive mode).
      if run_mode == gimpenums.RUN_WITH_LAST_VALS:
        _export_layer( gimpenums.RUN_INTERACTIVE,
                       overwrite_mode,
                       image,
                       layer,
                       filename )
      else:
        pdb.gimp_image_delete(image)
        raise ExportLayersError(e.message)

def _copy_layer(image, layer):
  layer_copy = pdb.gimp_layer_new_from_drawable(layer, image)
  pdb.gimp_image_insert_layer(image, layer_copy, None, 0)
  image.active_layer = layer_copy

  # Remove " copy" suffix from the layer copy (so that file formats supporting
  # layers do not display the " copy" suffix in the layer name).
  COPY_SUFFIX = " copy"
  if layer_copy.name.endswith(COPY_SUFFIX) and not layer.name.endswith(COPY_SUFFIX):
    layer_copy.name = layer_copy.name.rstrip(COPY_SUFFIX)

  return layer_copy

#===============================================================================

def export_layers( run_mode,
                   image,
                   file_format,
                   output_directory,
                   overwrite_mode,
                   ignore_invisible = False,
                   is_autocrop = False,
                   use_image_size = False ):
  """
  Export layers from specified image.
  
  Parameters:
  run_mode -- the run mode
  image -- GIMP image to export layers from
  file_format -- file extension indicating file format for exported layers
  output_directory -- output directory for exported layers
  overwrite_mode -- OverwriteMode object determining how to overwrite existing files
  
  Keyword arguments:
  ignore_invisible -- do not export invisible layers, including visible layers
                      within invisible layer groups (default False)
  is_autocrop -- autocrop layers before exporting (default False)
  use_image_size -- resize layers to image size before exporting (default False)
  """
  # Save context just in case. No need for undo groups or undo freeze here.
  pdb.gimp_context_push()
    
  ALLOWED_FILENAME_CHARS = string.ascii_letters + string.digits + '^&\'@{}[],$=!-#()%.+~_ '
  delete_table = string.maketrans(ALLOWED_FILENAME_CHARS, '\x00' * len(ALLOWED_FILENAME_CHARS))
  
  file_ext = file_format.lower()
  if not file_ext.startswith('.'):
    file_ext = '.' + file_ext

  # Perform subsequent operations on a new image so that the original image
  # and its soon-to-be exported layers are left intact.
  image_new = pdb.gimp_image_new(image.width, image.height, gimpenums.RGB)

  thumb_size = 128

  scale_x = float(thumb_size) / image.width
  scale_y = float(thumb_size) / image.height
  scale = min(scale_x, scale_y)
  new_width = round(scale * image.width)
  new_height = round(scale * image.height)

  image_thumb = pdb.gimp_image_new(new_width, new_height, gimpenums.RGB)
  self = {'run_mode': run_mode}

  def _writeLayerWithThumb(layer, suffix):
    _export_layer( self['run_mode'],
                   overwrite_mode,
                   image_new,
                   layer,
                   os.path.join( output_directory,
                                 base_name + suffix + file_ext ) )

    self['run_mode'] = gimpenums.RUN_WITH_LAST_VALS

    pdb.gimp_layer_scale(layer, new_width, new_height, False)
    pdb.gimp_layer_resize(layer, new_width, new_height, 0, 0)

    _export_layer( self['run_mode'],
                   overwrite_mode,
                   image_thumb,
                   layer,
                   os.path.join( output_directory,
                                 base_name + suffix + "_thumb" + file_ext ) )

    pdb.gimp_image_remove_layer(image_new, layer)

  i = 0
  for layer in image.layers:
    i += 1

    if (ignore_invisible and not pdb.gimp_item_get_visible(layer)):
      continue

#    if not use_image_size:
#      pdb.gimp_image_resize_to_layers(image_new)
#      if is_autocrop:
#        pdb.plug_in_autocrop(image_new, layer_copy)
#    else:
#      if is_autocrop:
#        pdb.plug_in_autocrop_layer(image_new, layer_copy)
#      pdb.gimp_layer_resize_to_image_size(layer_copy)

    # Allow only alphanumeric and certain special characters in filename.
    base_name = layer.name.translate(None, delete_table)
    # Remove file extension from output layer name if identical to the specified file format.
    name_parts = os.path.splitext(base_name)
    if name_parts[1] and name_parts[1] == file_ext:
      base_name = name_parts[0]

    # Create highlights for all but the base layer
    if i < len(image.layers):

      # Region border
      #
      # Detect edges, make them thicker, red and smooth
      layer_copy = _copy_layer(image_new, layer)
      pdb.plug_in_sobel(image_new, layer_copy, True, True, True)
      pdb.plug_in_convmatrix(image_new, layer_copy, 25, [1] * 25, False, 1, 0, 5, [1] * 5, 0)
      pdb.plug_in_colorify(image_new, layer_copy, (1.0, 1.0, 1.0, 1.0))
      pdb.plug_in_gauss_iir(image_new, layer_copy, 5, 1, 1)
      _writeLayerWithThumb(layer_copy, "_border")

      # Region highlight
      #
      # Make highlight white and blur it
      layer_copy = _copy_layer(image_new, layer)
      pdb.gimp_equalize(layer_copy, False)
      pdb.plug_in_gauss_iir(image_new, layer_copy, 5, 1, 1)
      _writeLayerWithThumb(layer_copy, "_area")

    else:
      layer_copy = _copy_layer(image_new, layer)
      _writeLayerWithThumb(layer_copy, "")

  pdb.gimp_image_delete(image_new)
  pdb.gimp_image_delete(image_thumb)
  pdb.gimp_context_pop()

#===============================================================================

# For testing purposes only.
# FILE_FORMATS = ['pix', 'matte', 'mask', 'alpha', 'als', 'txt', 'ansi', 'text', 'fli',
#                 'flc', 'xhtml', 'c', 'h', 'dds', 'dcm', 'dicom', 'eps', 'fit', 'fits',
#                 'gif', 'gbr', 'gih', 'xjt', 'xjtgz', 'xjtbz2', 'pat', 'html', 'htm', 'jpg',
#                 'jpeg', 'jpe', 'cel', 'ico', 'mng', 'ora', 'pbm', 'pgm', 'png', 'psd',
#                 'pnm', 'pdf', 'ps', 'ppm', 'sgi', 'rgb', 'rgba', 'bw', 'icon', 'im1',
#                 'im8', 'im24', 'im32', 'rs', 'ras', 'tga', 'tif', 'tiff', 'bmp', 'xmc',
#                 'xcf', 'xbm', 'icon', 'bitmap', 'xpm', 'xwd', 'pcx', 'pcc']

class ExportLayersGui(object):
  
  def __init__(self, image):
    self.image = image
    
    self.dialog = gimpui.Dialog(PLUGIN_TITLE, None, None, 0, None, None)
    self.dialog.set_transient()
    self.dialog.set_default_size(750,600)
    self.dialog.set_border_width(8)
    dialog_position = retrieve_setting('dialog_position')
    if dialog_position is not None:
      self.dialog.move(*dialog_position)
    
    self.directory_chooser_label = gtk.Label()
    self.directory_chooser_label.set_markup("<b>Choose output directory</b>")
    self.directory_chooser_label.set_alignment(0.0, 0.5)
    
    self.directory_chooser = gtk.FileChooserWidget(action=gtk.FILE_CHOOSER_ACTION_SELECT_FOLDER)
    
    self.hbox_file_format = gtk.HBox(homogeneous=False)
    self.hbox_file_format.set_spacing(5) 
    self.file_format_label = gtk.Label()
    self.file_format_label.set_markup("<b>File format:</b>")
    self.file_format_label.set_alignment(0.0, 0.5)
    self.file_format_label.set_size_request(100, -1)
    self.file_format_entry = gtk.Entry()
    self.file_format_entry.set_size_request(100, -1)
    self.file_format_error_icon = gtk.Image()
    self.file_format_error_icon.set_from_stock(gtk.STOCK_STOP, gtk.ICON_SIZE_BUTTON)
    self.file_format_error_message = gtk.Label()
    self.file_format_error_message.set_alignment(0.0, 0.5)
    self.hbox_file_format.pack_start(self.file_format_label, expand=False)
    self.hbox_file_format.pack_start(self.file_format_entry, expand=False)
    self.hbox_file_format.pack_start(self.file_format_error_icon, expand=False)
    self.hbox_file_format.pack_start(self.file_format_error_message, expand=False)
    
    self.hbox_export_options = gtk.HBox(homogeneous=False)
    self.export_options_ignore_invisible = gtk.CheckButton("Ignore invisible layers")
    self.export_options_is_autocrop = gtk.CheckButton("Autocrop layers")
    self.export_options_use_image_size = gtk.CheckButton("Use image size instead of layer size")
    self.hbox_export_options.pack_start(self.export_options_ignore_invisible)
    self.hbox_export_options.pack_start(self.export_options_is_autocrop)
    self.hbox_export_options.pack_start(self.export_options_use_image_size)
    
    self.hbox_action_area = gtk.HBox(homogeneous=False)
    self.export_layers_button = gtk.Button(label="Export Layers")
    self.export_layers_button.set_size_request(110, -1)
    self.cancel_button = gtk.Button(label="Cancel")
    self.cancel_button.set_size_request(110, -1)
    self.progress_bar = gimpui.ProgressBar()
#     self.progress_bar = gtk.ProgressBar()

    self.hbox_action_area.set_spacing(8)
    self.hbox_action_area.set_border_width(8)
    self.hbox_action_area.pack_start(self.progress_bar, expand=True, fill=True)
    self.hbox_action_area.pack_end(self.export_layers_button, expand=False, fill=True)
    self.hbox_action_area.pack_end(self.cancel_button, expand=False, fill=True)
    
    self.dialog.vbox.set_spacing(3)
    self.dialog.vbox.pack_start(self.directory_chooser_label, expand=False, fill=False)
    self.dialog.vbox.pack_start(self.directory_chooser, padding=5)
    self.dialog.vbox.pack_start(self.hbox_file_format, expand=False, fill=False)
    self.dialog.vbox.pack_start(self.hbox_export_options, expand=False, fill=False)
    self.dialog.vbox.pack_start(self.hbox_action_area, expand=False, fill=False)
    
    self.dialog.connect("response", self.response)
    self.cancel_button.connect("clicked", self.cancel)
    self.export_layers_button.connect("clicked", self.export)
    
    # Assign last used values if last export was successful and
    # if there are any, otherwise use default values.
    selected_directory = retrieve_setting('output_directory')
    if selected_directory is not None:
      self.directory_chooser.set_current_folder(selected_directory)
    else:
      if self.image.uri is not None:
        self.directory_chooser.set_uri(self.image.uri)
      else:
        self.directory_chooser.set_current_folder(ExportLayersPlugin.PARAMS_DEFAULT_VALUES['output_directory'])
    self.file_format_entry.set_text(
          retrieve_setting('file_format', ExportLayersPlugin.PARAMS_DEFAULT_VALUES['file_format']))
    self.export_options_ignore_invisible.set_active(
          retrieve_setting('ignore_invisible', ExportLayersPlugin.PARAMS_DEFAULT_VALUES['ignore_invisible']))
    self.export_options_is_autocrop.set_active(
          retrieve_setting('is_autocrop', ExportLayersPlugin.PARAMS_DEFAULT_VALUES['is_autocrop']))
    self.export_options_use_image_size.set_active(
          retrieve_setting('use_image_size', ExportLayersPlugin.PARAMS_DEFAULT_VALUES['use_image_size']))
    
    self.dialog.show_all()
    # Action area is unused, the dialog bottom would otherwise be filled with empty space.
    self.dialog.action_area.hide()

    self.display_label_error_message()
    self.progress_bar.set_visible(False)
  
  def export(self, widget):
    file_format = self.file_format_entry.get_text()
    
    if file_format is not None and file_format:
      self.display_label_error_message()
    else:
      self.display_label_error_message("File format not specified.")
      return
    
    output_directory = self.directory_chooser.get_current_folder()
    overwrite_mode = DialogOverwrite(parent=self.dialog)
    ignore_invisible = self.export_options_ignore_invisible.get_active()
    is_autocrop = self.export_options_is_autocrop.get_active()
    use_image_size = self.export_options_use_image_size.get_active()

    self.progress_bar.set_visible(True)
    pdb.gimp_progress_init("Exporting...", None)
    try:
      export_layers( gimpenums.RUN_INTERACTIVE,
                     self.image,
                     file_format,
                     output_directory,
                     overwrite_mode,
                     ignore_invisible, is_autocrop, use_image_size )
      # For testing purposes only.
#       for j in range(0,2):
#         file_format_counter = retrieve_setting('file_format_counter', 0)
#         file_format = FILE_FORMATS[file_format_counter]
#         export_layers(gimpenums.RUN_INTERACTIVE, self.image,
#                                         file_format, os.path.join(output_directory, file_format),
#                                         overwrite_mode,
#                                         ignore_invisible, is_autocrop, use_image_size)
#       file_format_counter += 1
#       store_settings(file_format_counter=file_format_counter)
      
    except ExportLayersCancelError as e:
      return
    except ExportLayersError as e:
      error_message = 'Error: file format "' + file_format + '": ' + e.message
      if not e.message.endswith('.'):
        error_message += '.'
      self.display_label_error_message(error_message)
      return
    except Exception as e:
      display_message(e.message)
    finally:
      self.progress_bar.set_visible(False)
      pdb.gimp_progress_end()
    
    store_settings( image=self.image,
                    file_format=file_format,
                    output_directory=output_directory,
                    overwrite_mode=overwrite_mode.get_value(),
                    ignore_invisible=ignore_invisible,
                    is_autocrop=is_autocrop,
                    use_image_size=use_image_size,
                    dialog_position=self.dialog.get_position(),
                    first_run=False )
    
    gtk.main_quit()
  
  def response(self, widget, response_id):
    gtk.main_quit()
  
  def cancel(self, widget):
    gtk.main_quit()
  
  def display_label_error_message(self, text=None):
    if text is None:
      self.file_format_error_message.set_text("")
      self.file_format_error_icon.set_visible(False)
    else:
      self.file_format_error_icon.set_visible(True)
      self.file_format_error_message.set_markup('<span foreground="red">' + text + '</span>')

#===============================================================================

def store_settings(**kwargs):
  for key, value in kwargs.items():
    shelf[SHELF_PREFIX + key] = value

def retrieve_setting(key, default_value=None):
  value = default_value
  try:
    value = shelf[SHELF_PREFIX + key]
  except KeyError:
    return default_value
  else:
    return value

def retrieve_settings(*keys):
  settings = []
  
  for key in keys:
    value = None
    try:
      value = shelf[SHELF_PREFIX + key]
    except KeyError:
      # Ignore settings not found
      pass
    else:
      settings.append(value)
  
  return settings

#===============================================================================

class ExportLayersPlugin(gimpplugin.plugin):
 
  PLUG_IN_EXPORT_LAYERS_PARAMS =   [
   (gimpenums.PDB_INT32, "run_mode", "The run mode { RUN-INTERACTIVE (0), RUN-NONINTERACTIVE (1) }"),
   (gimpenums.PDB_IMAGE, "image", "Image to export layers from"),
   (gimpenums.PDB_STRING, "file_format", "File format"),
   (gimpenums.PDB_STRING, "output_directory", "Output directory"),
   (gimpenums.PDB_INT32, "ignore_invisible", "Ignore invisible layers"),
   (gimpenums.PDB_INT32, "is_autocrop", "Autocrop layers"),
   (gimpenums.PDB_INT32, "use_image_size", "Use image size instead of layer size"),
   (gimpenums.PDB_INT32, "overwrite_mode", ("Overwrite mode (non-interactive only)"
                                            "{ 1 = Skip, 2 = Overwrite, 3 = Rename new files, 4 = Rename existing files}")),
   ]
  PARAMS_DEFAULT_VALUES = { 'file_format' : "png",
                            'output_directory' : gimp.user_directory(4),    # Pictures directory
                            'overwrite_mode' : OverwriteMode.OVERWRITE,
                            'ignore_invisible' : False,
                            'is_autocrop' : False,
                            'use_image_size' : False,
                           }
  
  PLUG_IN_RETS = [

   ]
 
  def query(self):
    gimp.install_procedure("plug_in_export_layers",
                           "Export layers as separate images in specified file format to specified directory.",
                           "Layer names are used as filenames for the exported images.",

                           "khalim19",
                           "khalim19",
                           "2013",
                           "<Image>/File/Export/E_xport Layers (HTI)...",
                           "*",
                           gimpenums.PLUGIN,
                           self.PLUG_IN_EXPORT_LAYERS_PARAMS,
                           self.PLUG_IN_RETS
                           )
   
  def plug_in_export_layers(self, run_mode, image, file_format=None, output_directory=None, 
                            ignore_invisible=False, is_autocrop=False, use_image_size=False,
                            overwrite_mode=OverwriteMode.OVERWRITE):
    if run_mode == gimpenums.RUN_INTERACTIVE:
      gui = ExportLayersGui(image)
      gtk.main()
    elif run_mode == gimpenums.RUN_WITH_LAST_VALS:
      first_run = retrieve_setting('first_run', True)
      if first_run:
        raise ExportLayersError("No last values specified. Use RUN_INTERACTIVE or RUN_NONINTERACTIVE run mode")
      export_layers(
        run_mode, image,
        retrieve_setting('file_format', self.PARAMS_DEFAULT_VALUES['file_format']),
        retrieve_setting('output_directory', self.PARAMS_DEFAULT_VALUES['output_directory']),
        NoninteractiveOverwrite(retrieve_setting('overwrite_mode', self.PARAMS_DEFAULT_VALUES['overwrite_mode'])),
        retrieve_setting('ignore_invisible', self.PARAMS_DEFAULT_VALUES['ignore_invisible']),
        retrieve_setting('is_autocrop', self.PARAMS_DEFAULT_VALUES['is_autocrop']),
        retrieve_setting('use_image_size', self.PARAMS_DEFAULT_VALUES['use_image_size']),
        )
    else:          # gimpenums.RUN_NONINTERACTIVE
      if output_directory is None:
        output_directory = self.PARAMS_DEFAULT_VALUES['output_directory']
      if file_format is None:
        file_format = self.PARAMS_DEFAULT_VALUES['file_format']
      if overwrite_mode not in OverwriteMode.OVERWRITE_MODES:
        overwrite_mode = self.PARAMS_DEFAULT_VALUES['overwrite_mode']
      export_layers( run_mode, image, file_format, output_directory,
                     NoninteractiveOverwrite(overwrite_mode),
                     ignore_invisible, is_autocrop, use_image_size)
      store_settings(first_run=False)

if __name__ == "__main__":
  ExportLayersPlugin().start()
