#!/bin/bash

module load nco
module load cdo

# Extract last time step
ncks -d Time,-1,-1 wrfout_d01_2000-01-24_21:00:00 restart_out.nc
ncks -d Time,-1,-1 /g/data/sx70/data/KGO/4.1.1/jan00/wrfout_d01_2000-01-24_12:00:00 jan00_out.nc

cdo diffn restart_out.nc jan00_out.nc