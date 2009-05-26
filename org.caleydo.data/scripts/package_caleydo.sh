#!/bin/bash
# Copyright (C) 2009 Alexander Lex - alexander.lex@icg.tugraz.at
#

mount_point="/mnt/webdav"

copy_to_web()
{

  already_mounted=false
  for i in `cat /proc/mounts | cut -d' ' -f2`; do
    if [ "$mount_point" = "$i" ]; then
      already_mounted=true
      echo "$mount_point is already a mount point"    
    fi
  done

  if [ "$already_mounted" = "false" ]; then
    sudo mount -t davfs https://caleydo.icg.tugraz.at/dav/ /mnt/webdav
  fi

  sudo cp  $export_path /mnt/webdav/htdocs/download -R

}

make_archive()
{  
  #echo $export_path
  mkdir -p $export_path #{"win_vista","win_xp","linux"}

  echo $export_path/caleydo_$version_number"_linux_x86.tar.gz"
  echo $export_root/linux.gtk.x86/caleydo/*
  tar -czvf $export_path/caleydo_$version_number"_linux_x86.tar.gz" -C $export_root/linux.gtk.x86/caleydo/*
  zip -r $export_path/caleydo_$version_number"_win_xp_x86.zip" $export_root/win32.win32.x86/caleydo/*
  zip -r $export_path/caleydo_$version_number"_win_vista_x86.zip" $export_root/win32.wpf.x86/caleydo/*
}

print_help()
{
  echo "exort.sh - packages calyedo and copies it to the web"
  echo "Usage: export.sh [ option ]"
  echo ""
  echo "Options:"
  echo "-a make archive"
  echo "-c copy to web"
  echo ""
  echo "If none of the options is present all first the archive is made then the result is copied to the web"
}

echo "Caleydo packaging"
echo "Enter the version number for the export"
read version_number

export_root=$HOME"/caleydo_export"
export_path=$export_root/$version_number

#echo $*

#$# number of params
#$1 first param

if [ $# -gt 0 ]
then
  count=0
  while [ "$count" -lt $# ]
  do
    case $1 in
      # rename
      -a) make_archive "y";;
      # resize
      -c) copy_to_web "y";;
      # command not recognized
      *) print_help;;
    esac
    shift
    count=$(($count + 1))
  done
else

  make_archive
  echo "Do you want to copy the release to the website? (y/n)"
  read copy
  if [ $copy = "y" ];
    then
    copy_to_web
  fi
fi