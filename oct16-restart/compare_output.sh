#!/bin/bash

module load nco
module load cdo

# Extract last time step
ncks -d Time,-1,-1 wrfout_d01_2016-10-06_06:00:00 restart_out.nc
ncks -d Time,-1,-1 /g/data/sx70/data/KGO/4.4/oct16/wrfout_d01_2016-10-06_00:00:00 oct16_out.nc

cdo diffn restart_out.nc oct16_out.nc
