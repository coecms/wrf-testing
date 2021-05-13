#!/bin/bash

module load nco
module load cdo

# Extract last time step
nco -d Time,-1,-1 wrfout_d01_2000-01-24_18:00:00 restart_out.nc
nco -d Time,-1,-1 $path/jan00/wrfout_d01_2000-01-24_12:00:00 jan00_out.nc

cdo diffn restart_out.nc jan00_out.nc