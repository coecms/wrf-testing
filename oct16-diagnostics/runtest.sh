#!/bin/bash
# Runs the October 2016 WRF test case
# Path to WPS and WRFV3 directories should be passed as WRF_ROOT

#PBS -q normal
#PBS -l walltime=15:00
#PBS -l ncpus=28
#PBS -l mem=25gb
#PBS -l wd
#PBS -l storage=scratch/w35+gdata/sx70
#PBS -W umask=0022

set -eu

if [ -x "${WRF_ROOT}/WRF/main/real.exe" ]; then
    export PATH="${WRF_ROOT}/WRF/main:${PATH}"
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
cp namelists/namelist.input namelist.input
cp namelists/namelist.wps namelist.wps

# Link GEOGRID table
[[ -d geogrid ]] || mkdir geogrid
ln -sf ${WRF_ROOT}/WPS/geogrid/GEOGRID.TBL geogrid/.

mpirun -np 4 geogrid.exe

link_grib.csh /g/data/sx70/data/SingleDomain_data/matthew/fnl
# Link Vtable
ln -sf ${WRF_ROOT}/WPS/ungrib/Variable_Tables/Vtable.GFS Vtable

ungrib.exe

# Link METGRID table
[[ -d metgrid ]] || mkdir metgrid
ln -sf ${WRF_ROOT}/WPS/metgrid/METGRID.TBL metgrid/.

mpirun -np 4 metgrid.exe

mpirun real.exe

mpirun wrf.exe

echo "Exit code from WRF: " $?
echo "Test run finished"
