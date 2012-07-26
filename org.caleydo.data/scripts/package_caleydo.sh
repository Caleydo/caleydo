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
# Copyright (C) 2009 Alexander Lex - alexander.lex@icg.tugraz.at
#


# Requires alien to be installed. Run  sudo apt-get install alien

mount_point="/mnt/webdav"
download_folder=$mount_point"/download"
webstart_plugins_folder=$download_folder"/webstart_2.0/plugins"

export_root=$HOME"/caleydo_export"
webstart_export_path=$export_root"/webstart_2.0"
debian_folder=$(pwd)"/debian"
linux_folder=""
linux_source_folder=""
arch=""

echo $debian_folder

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

copy_to_web()
{
  sudo cp $export_path $download_folder -R -v
}

copy_webstart()
{
  sudo cp $webstart_export_path"/plugins/org.caleydo."* $webstart_plugins_folder -v
}

make_archive()
{  
  #echo $export_path
  mkdir -p $export_path #{"win_vista","win_xp","linux"}

  #echo $export_path/caleydo_$version_number"_linux_x86-32.tar.gz"
  #echo $export_root/linux.gtk.x86/caleydo/*
  tar -czvf $export_path/caleydo_$version_number"_linux_x86-32.tar.gz" -C $export_root/linux.gtk.x86/ caleydo
  tar -czvf $export_path/caleydo_data_importer_$version_number"_linux_x86-32.tar.gz" -C $export_root/linux.gtk.x86/ caleydo_data_importer
 
  tar -czvf $export_path/caleydo_$version_number"_linux_x86-64.tar.gz" -C $export_root/linux.gtk.x86_64/ caleydo
  tar -czvf $export_path/caleydo_data_importer_$version_number"_linux_x86-64.tar.gz" -C $export_root/linux.gtk.x86_64/ caleydo_data_importer  
  
  cd $export_root/win32.win32.x86/
  zip -r $export_path/caleydo_$version_number"_win_x86-32.zip" caleydo
  zip -r $export_path/caleydo_data_importer_$version_number"_win_x86-32.zip" caleydo_data_importer
  
  cd $export_root/win32.win32.x86_64/
  zip -r $export_path/caleydo_$version_number"_win_x86-64.zip" caleydo 
  zip -r $export_path/caleydo_data_importer_$version_number"_win_x86-64.zip" caleydo_data_importer 
  
  cd $export_root/macosx.cocoa.x86/
  zip -r $export_path/caleydo_$version_number"_macosx_cocoa_x86-32.zip" caleydo
  zip -r $export_path/caleydo_data_importer_$version_number"_macosx_cocoa_x86-32.zip" caleydo_data_importer
  
  cd $export_root/macosx.cocoa.x86_64/
  zip -r $export_path/caleydo_$version_number"_macosx_cocoa_x86-64.zip" caleydo 
  zip -r $export_path/caleydo_data_importer_$version_number"_macosx_cocoa_x86-64.zip" caleydo_data_importer
}

# Trigger creation of debian packages for architectures x86-32 and x86-64
make_debian_packages()
{
  mkdir -p $export_path
  linux_source_folder=$export_root/linux.gtk.x86/
  linux_folder="caleydo_"$version_number"_linux_x86-32"
  arch="i386"
  do_deb
  
  linux_source_folder=$export_root/linux.gtk.x86_64/
  linux_folder="caleydo_"$version_number"_linux_x86-64"
  arch="amd64"
  do_deb
  
}

# create deb and rpm for specific platform
do_deb()
{
  echo -n "Creating Linux packages for "$arch 
  cd $export_root
  cp $debian_folder  $linux_folder -R
  cp $linux_source_folder $linux_32_folder"/opt/caleydo"
  #cp eclipse/ $linux_folder"/opt/caleydo" -R
  sed -i 's/ARCH_STRING/'$arch'/g' $linux_folder"/DEBIAN/control"
  sed -i 's/VERSION_NUMBER/'$version_number'/g' $linux_folder"/DEBIAN/control"
  dpkg-deb --build $linux_folder  > /dev/null
  sudo alien -r $linux_folder".deb" > /dev/null
  rm $linux_folder -R
  mv *.deb *.rpm $export_path
  echo ".... [x] done"
}

print_help()
{
  echo "exort.sh - packages calyedo and copies it to the web"
  echo "Usage: export.sh [ option ]"
  echo ""
  echo "Options:"
  echo "-a package standalone"
  echo "-c copy standalone"
  echo "-s package and copy standalone"
  echo "-w copy webstart"
  echo ""
  echo "If none of the options is present all are run: first the archive is made then the result is copied to the web, then the webstart is copied"
}

ask_for_version()
{
  echo "Caleydo packaging"
  echo "Enter the version number for the export"
  read version_number
  export_path=$export_root/$version_number
}
#echo $*

#$# number of params
#$1 first param


if [ $# -gt 0 ]
then
  count=0
  while [ "$count" -lt $# ]
  do
    case $1 in
      -d) ask_for_version
          make_debian_packages;;
      # package standalone
      -a) ask_for_version
	  make_archive
	  make_debian_packages;;
      # copy standalone
      -c) ask_for_version
	  mount_webdav
	  copy_to_web;;
      # package and copy standalone
      -s) ask_for_version
	  make_archive
	  mount_webdav
	  copy_to_web;;
      # copy webstart
      -w) mount_webdav
	  copy_webstart;;
      # command not recognized
      *) print_help;;
    esac
    shift
    count=$(($count + 1))
  done
else

  echo "Do you want to package the standalone release? (y/n)"
  read copy
  if [ $copy = "y" ];
    then
    ask_for_version
    make_archive
    echo "Do you want to copy the release to the website? (y/n)"
    read copy
    if [ $copy = "y" ];
      then
      mount_webdav
      copy_to_web
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