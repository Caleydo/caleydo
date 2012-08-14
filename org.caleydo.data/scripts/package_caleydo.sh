#*******************************************************************************
# Caleydo - visualization for molecular biology - http://caleydo.org
#  
# Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
# Lex, Christian Partl, Johannes Kepler University Linz </p>
#
# This program is free software: you can redistribute it and/or modify it under
# the terms of the GNU General Public License as published by the Free Software
# Foundation, either version 3 of the License, or (at your option) any later
# version.
#  
# This program is distributed in the hope that it will be useful, but WITHOUT
# ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
# FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
# details.
#  
# You should have received a copy of the GNU General Public License along with
# this program. If not, see <http://www.gnu.org/licenses/>
#*******************************************************************************
#!/bin/bash
# Copyright (C) 2009, 2012 Alexander Lex - alexander.lex@icg.tugraz.at
#


# Requires alien to be installed. Run  sudo apt-get install alien

mount_point="/mnt/webdav"
# the folder on the webserver where the packages are placed
download_folder=$mount_point"/download"
# the folder where the binaries are after the eclipse export
export_root=$HOME"/caleydo_export"

# the folder where the debian package specific stuff is stored, which is relative to the location of this script
debian_folder=$(pwd)"/debian"

#======= The platform specific binary folders ========

linux32_folder=$export_root"/linux.gtk.x86"
linux64_folder=$export_root"/linux.gtk.x86_64"

win32_folder=$export_root"/win32.win32.x86"
win64_folder=$export_root"/win32.win32.x86_64"

mac32_folder=$export_root"/macosx.cocoa.x86"
mac64_folder=$export_root"/macosx.cocoa.x86_64"

#======================================================

# the name of the product, corresponding also to the directory inside the binary folders. 
# eg $linux32_folder"/"$product should be the folder to the binaries
product="caleydo"


# mounts the web server locally
mount_webdav()
{
  already_mounted=false
  for i in `cat /proc/mounts | cut -d' ' -f2`; do
    if [ "$mount_point" = "$i" ]; then
      already_mounted=true
      echo "$mount_point is already a mount point"    
    fi
  done

  if [ "$already_mounted" = "false" ]; then
    sudo mount -t davfs https://data.icg.tugraz.at/caleydo/dav/ $mount_point
  fi
}

# copies the standalone packages to the web server
copy_standalone()
{
  sudo cp $export_path $download_folder -R -v
}

# copies the webstart parts to the webserver
copy_webstart()
{
  webstart_export_path=$export_root"/webstart_"$version_number
  webstart_plugins_folder=$download_folder"/webstart/plugins"

  sudo cp $webstart_export_path"/plugins/org.caleydo."* $webstart_plugins_folder -v
}

# moves the windows installer if it exists
move_windows_installer()
{
 echo -n "Moving Windows installer for x86"
 setup_file=$win32_folder"/"$product"/Setup.exe"
 target_setup_file=$export_path"/"$product"_"$version_number"_installer_win_x86-32.exe"
 move_file
 echo ".... [x] done"
 
  echo -n "Moving Windows installer for x86-64"
 setup_file=$win64_folder"/"$product"/Setup.exe"
 target_setup_file=$export_path"/"$product"_"$version_number"_installer_win_x86-64.exe"
 move_file
 echo ".... [x] done"
}
# helper for moving a file if it exists
move_file()
{
  if [ -f $setup_file ]
  then
      mv $setup_file $target_setup_file
  else
      echo "Error: Windows setup file does not exist: "$setup_file
  fi
}

# create zip packages of the binaries
create_zip_packages()
{  
  echo -n "Creating linux_x86-32 tar"
  tar -czf $export_path/caleydo_$version_number"_linux_x86-32.tar.gz" -C $linux32_folder  caleydo
  echo ".... [x] done"
  
  echo -n "Creating linux_x86-64 tar"
  tar -czf $export_path/caleydo_$version_number"_linux_x86-64.tar.gz" -C $linux64_folder caleydo
  echo ".... [x] done"
 
  echo -n "Creating win32.x86 zip"
  cd $win32_folder
  zip -q -r $export_path/caleydo_$version_number"_win_x86-32.zip" caleydo
  echo ".... [x] done"
  
  echo -n "Creating win32.x86-64 zip"
  cd $win64_folder
  zip -q -r $export_path/caleydo_$version_number"_win_x86-64.zip" caleydo 
  echo ".... [x] done"
  
  echo -n "Creating macosx.cocoa.x86 zip"
  cd $mac32_folder
  zip -q -r $export_path/caleydo_$version_number"_macosx_cocoa_x86-32.zip" caleydo
  echo ".... [x] done"
  
  echo -n "Creating macosx.cocoa.x86-64 zip"
  cd $mac64_folder
  zip -q -r $export_path/caleydo_$version_number"_macosx_cocoa_x86-64.zip" caleydo 
  echo ".... [x] done"
  
}

# create zip packages of the importer bianries
# TODO fix this! It just does the same as create_zip_packages with different names
package_importer()
{

  tar -czf $export_path/caleydo_data_importer_$version_number"_linux_x86-32.tar.gz" -C $export_root/linux.gtk.x86/ caleydo_data_importer
 
  tar -czf $export_path/caleydo_data_importer_$version_number"_linux_x86-64.tar.gz" -C $export_root/linux.gtk.x86_64/ caleydo_data_importer  
  
  cd $export_root/win32.win32.x86/
  zip -q -r $export_path/caleydo_data_importer_$version_number"_win_x86-32.zip" caleydo_data_importer
  
  cd $export_root/win32.win32.x86_64/
  zip -q -r $export_path/caleydo_data_importer_$version_number"_win_x86-64.zip" caleydo_data_importer 
  
  cd $export_root/macosx.cocoa.x86/
  zip -q -r $export_path/caleydo_data_importer_$version_number"_macosx_cocoa_x86-32.zip" caleydo_data_importer
  
  cd $export_root/macosx.cocoa.x86_64/
  zip -q -r $export_path/caleydo_data_importer_$version_number"_macosx_cocoa_x86-64.zip" caleydo_data_importer
}

# Trigger creation of debian packages for architectures x86-32 and x86-64
create_linux_packages()
{
  linux_source_folder=$linux32_folder
  linux_folder="caleydo_"$version_number"_linux_x86-32"
  arch="i386"
  do_deb
  
  linux_source_folder=$linux64_folder
  linux_folder="caleydo_"$version_number"_linux_x86-64"
  arch="amd64"
  do_deb  
}

# helper to create deb and rpm for specific platform
do_deb()
{
  echo -n "Creating Linux packages for "$arch 
  cd $export_root
  cp $debian_folder  $linux_folder -R
  # remove .svn files from debian folder
  rm -rf `find $linux_folder -type d -name .svn`
  
  cp $linux_source_folder"/"$product $linux_folder"/opt/" -R
  sed -i 's/ARCH_STRING/'$arch'/g' $linux_folder"/DEBIAN/control"
  sed -i 's/VERSION_NUMBER/'$version_number'/g' $linux_folder"/DEBIAN/control"
  sed -i 's/VERSION_NUMBER/'$version_number'/g' $linux_folder"/usr/share/applications/caleydo.desktop"
  sudo chgrp root $linux_folder -R
  sudo chown root $linux_folder -R
  dpkg-deb --build $linux_folder  > /dev/null
  sudo alien -r --scripts $linux_folder".deb" > /dev/null
  sudo rm $linux_folder -Rf
  mv *.deb *.rpm $export_path
  echo ".... [x] done"
}

print_help()
{
  echo "exort.sh - packages calyedo and copies it to the web"
  echo "Usage: export.sh [ option ]"
  echo ""
  echo "Options:"
  echo "-a package standalone zips, debs and rpms"
  echo "-d package standalone debs and rpms"
  echo "-c copy standalone"
  echo "-s package, and copy standalone"
  echo "-w copy webstart"
  echo ""
  echo "If none of the options is present all are run: first the archive is made then the result is copied to the web, then the webstart is copied"
}

# reads the version number and creates the export path
ask_for_version()
{
  echo "Caleydo packaging"
  echo "Enter the version number for the export"
  read version_number
  export_path=$export_root/$version_number
  mkdir -p $export_path
}

# checking for input parameters

if [ $# -gt 0 ]
then
  count=0
  while [ "$count" -lt $# ]
  do
    case $1 in
      # create only linux packages
      -d) ask_for_version
          create_linux_packages;;
      # package everything standalone
      -a) ask_for_version
	  move_windows_installer
	  create_zip_packages
	  create_linux_packages;;
      # copy standalone
      -c) ask_for_version
	  mount_webdav
	  copy_standalone;;
      # package and copy standalone
      -s) ask_for_version
	  move_windows_installer
	  create_zip_packages
	  create_linux_packages
	  mount_webdav
	  copy_standalone;;
      # copy webstart
      -w) mount_webdav
	  copy_webstart;;
      # move and rename the windows installer
      -i) ask_for_version
	  move_windows_installer;;
      # command not recognized
      *) print_help;;
    esac
    shift
    count=$(($count + 1))
  done
else

  #interactive mode
  ask_for_version
  echo "Do you want to package the standalone release? (y/n)"
  read copy
  if [ $copy = "y" ];
    then
    move_windows_installer
    create_zip_packages
    create_linux_packages
    echo "Do you want to copy the release to the website? (y/n)"
    read copy
    if [ $copy = "y" ];
      then
      mount_webdav
      copy_standalone
    fi
  fi

  echo "Do you want to copy the webstart release to the website? (y/n)"
  read copy
  if [ $copy = "y" ];
    then
    mount_webdav
    copy_webstart
  fi
fi