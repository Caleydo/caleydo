#!/bin/sh

convert caleydo.svg -resize 512x512 caleydo_512.png

convert caleydo_512.png -resize 256x256 caleydo_256.png
convert caleydo_512.png -resize 128x128 caleydo_128.png
convert caleydo_512.png -resize 64x64 caleydo_64.png
convert caleydo_512.png -resize 48x48 caleydo_48.png
convert caleydo_512.png -resize 32x32 caleydo_32.png
convert caleydo_512.png -resize 24x24 caleydo_24.png
convert caleydo_512.png -resize 16x16 caleydo_16.png

convert caleydo_512.png -resize 48x48 -color 256 caleydo_48_8bit.png
convert caleydo_512.png -resize 32x32 -color 256 caleydo_32_8bit.png
convert caleydo_512.png -resize 16x16 -color 256 caleydo_16_8bit.png

convert caleydo_512.png -resize 48x48 caleydo_48.xpm

convert caleydo_256.png caleydo_48.png caleydo_48_8bit.png caleydo_32.png caleydo_32_8bit.png caleydo_16.png caleydo_16_8bit.png caleydo.ico

convert caleydo_512.png caleydo_256.png caleydo_128.png caleydo_64.png caleydo_48.png caleydo_32.png caleydo_16.png caleydo.icns

