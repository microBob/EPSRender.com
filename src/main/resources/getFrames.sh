#!/bin/bash
echo "$1"
blender -b "$1" --python "/home/microbobu/Documents/EPS Render Server/EPRender.com/src/main/resources/getFrames.py" #| grep -w "EPRenderInterestedFrames:"
echo "finished above"