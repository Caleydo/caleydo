#!/bin/bash
# Copyright (C) 2009 Alexander Lex - alexander.lex@icg.tugraz.at
#
# this script expects to be run from the root website svn repository directory

temp_dir="/tmp/caleydo_website"
temp_htdocs=$temp_dir"/htdocs"
mount_point="/mnt/webdav"
htdocs=$mount_point"/htdocs"


mount_website()
{
  already_mounted=false
  for i in `cat /proc/mounts | cut -d' ' -f2`; do
    if [ "$mount_point" = "$i" ]; then
      already_mounted=true
      echo "$mount_point is already a mount point"    
    fi
  done

  if [ "$already_mounted" = "false" ]; then
    #mkdir -p $mount_points
    sudo mount -t davfs https://caleydo.icg.tugraz.at/dav/ $mount_point
  fi
} 

update_website()
{
  current_directory=$PWD
  
  sudo rm $temp_dir -Rf
  sudo mkdir -p $temp_htdocs || exit
  sudo cp htdocs/* $temp_htdocs -R
  sudo cp images $temp_dir -R
  purge_svn_folders

  cd $htdocs
  ls | grep -v download | xargs sudo rm -Rv
  sudo rm downloads.html
  cd $mount_point
  sudo rm images -R
  cd $temp_dir
  sudo cp htdocs/* $htdocs -Rv
  sudo cp images $mount_point -Rv
}


purge_svn_folders()
{
  sudo find $temp_dir -type d -name .svn | xargs sudo rm -Rf  
  #sudo find $mount_point -type d -name .svn -exec rm -rfv {} \;
  #sudo find $mount_point -type d -name .svn
  #sudo rm -rfv `find $mount_point -type d -name .svn` 
}

update_dates()
{
  directory="htdocs"
  todays_date=$(date +%Y-%m-%d)

  find $directory -type f -name *.html -exec grep -l '20[0-9][0-9]-[0-9][0-9]-[0-9][0-9]' {} \; | while read file
  do
      sed "s/20[0-9][0-9]-[0-9][0-9]-[0-9][0-9]/$todays_date/ig" "$file" > tmp
      mv tmp "$file"
  done
}


print_help()
{
  echo "changes the date stamp in the files and copies them to the website"
  echo "Usage: update_website.sh [ option ]"
  echo ""
  echo "Options:"
  echo "-d update the dates of the html pages"
  echo "-c copy website"
  echo ""
  echo "If none of the options is present first the dates are updated and then the website is copied"
}


if [ $# -gt 0 ]
then
  count=0
  while [ "$count" -lt $# ]
  do
    case $1 in
      # update dates
      -d) update_dates;;
      # copy website
      -c) mount_website
	  update_website;;
      # command not recognized
      *) print_help;;
    esac
    shift
    count=$(($count + 1))
  done
else
  update_dates
  mount_website
  update_website
fi