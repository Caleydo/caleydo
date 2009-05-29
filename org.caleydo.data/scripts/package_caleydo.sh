#!/bin/bash
# Copyright (C) 2009 Alexander Lex - alexander.lex@icg.tugraz.at
#

mount_point="/mnt/webdav"
download_folder=$mount_point"/htdocs/download"
webstart_plugins_folder=$download_folder"/webstart/plugins"

export_root=$HOME"/caleydo_export"
webstart_export_path=$export_root"/webstart"

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
    sudo mount -t davfs https://caleydo.icg.tugraz.at/dav/ $mount_point
  fi
}

copy_to_web()
{
  sudo cp $export_path $download_folder -R
}

copy_webstart()
{
  sudo cp $webstart_export_path"/plugins/org.caleydo."* $webstart_plugins_folder
}

make_archive()
{  
  #echo $export_path
  mkdir -p $export_path #{"win_vista","win_xp","linux"}

  echo $export_path/caleydo_$version_number"_linux_x86.tar.gz"
  echo $export_root/linux.gtk.x86/caleydo/*
  tar -czvf $export_path/caleydo_$version_number"_linux_x86.tar.gz" -C $export_root/linux.gtk.x86/ caleydo
  cd $export_root/win32.win32.x86/
  zip -r $export_path/caleydo_$version_number"_win_xp_x86.zip" caleydo
  cd $export_root/win32.wpf.x86/
  zip -r $export_path/caleydo_$version_number"_win_vista_x86.zip" caleydo
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
  echo "If none of the options is present all first the archive is made then the result is copied to the web, then the webstart is copied"
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
      # package standalone
      -a) ask_for_version
	  make_archive;;
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