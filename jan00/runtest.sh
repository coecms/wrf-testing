#!/bin/bash
# Runs the JAN00 WRF test case
# Path to WPS and WRFV3 directories should be passed as WRF_ROOT

#PBS -q express
#PBS -l walltime=15:00
#PBS -l ncpus=4
#PBS -l mem=4gb
#PBS -l wd

set -eu

if [ -x "${WRF_ROOT}/WRFV3/main/real.exe" ]; then
    export PATH="${WRF_ROOT}/WRFV3/main:${PATH}"
else
    echo "ERROR: WRF not found"
    exit 1
fi
if [ -x "${WRF_ROOT}/WPS/geogrid.exe" ]; then
    export PATH="${WRF_ROOT}/WPS:${PATH}"
else
    echo "ERROR: WPS not found"
    exit 1
fi

module purge
module load openmpi

geogrid.exe

link_grib.csh /projects/WRF/data/JAN00/2000

ungrib.exe

metgrid.exe

real.exe

mpirun wrf.exe
