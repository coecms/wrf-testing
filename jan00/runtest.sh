#!/bin/bash
# Runs the JAN00 WRF test case
# Path to WPS and WRFV3 directories should be passed as WRF_ROOT

#PBS -q express
#PBS -l walltime=15:00
#PBS -l ncpus=4
#PBS -l mem=4gb
#PBS -l wd
#PBS -l storage=scratch/w35+gdata/sx70
#PBS -W umask=0022

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
module load openmpi/4.0.2

# Copy WRF run/ directory
cp ${WRF_ROOT}/WRF/run/* .

# Overwrite the namelists with the ones saved for the tests
cp namelists/namelist.input .
cp namelists/namelist.wps .

# Link GEOGRID table
ln -sf ${WRF_ROOT}/WPS/geogrid/GEOGRID.TBL geogrid/.

geogrid.exe

link_grib.csh /g/data/sx70/data/SingleDomain_data/matthew/fnl
# Link Vtable
ln -sf ${WRF_ROOT}/WPS/ungrib/Variable_Tables/Vtable.GFS Vtable

ungrib.exe

# Link METGRID table
ln -sf ${WRF_ROOT}/WPS/metgrid/METGRID.TBL metgrid/.

metgrid.exe

mpirun real.exe

mpirun wrf.exe
