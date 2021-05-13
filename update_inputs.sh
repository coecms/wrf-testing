#!/bin/bash
# To update WPS and WRF input files when changing version.

WRF_PATH=$1
# Update jan00 test
echo "Updating inputs for jan00"
echo "   Updating GEOGRID.TBL"
cp $WRF_PATH/WPS/geogrid/GEOGRID.TBL.ARW jan00/geogrid/GEOGRID.TBL
echo "   Updating METGRID.TBL"
cp $WRF_PATH/WPS/metgrid/METGRID.TBL.ARW jan00/metgrid/METGRID.TBL
echo "   Updating WRF run/"
cp $WRF_PATH/WRF/run/* jan00/.
rm jan00/run_mpi jan00/run_real

# Update jan00-diagnostics
echo "Updating inputs for jan00-diagnostics"
echo "   Updating GEOGRID.TBL"
cp $WRF_PATH/WPS/geogrid/GEOGRID.TBL.ARW jan00-diagnostics/geogrid/GEOGRID.TBL
echo "   Updating METGRID.TBL"
cp $WRF_PATH/WPS/metgrid/METGRID.TBL.ARW jan00-diagnostics/metgrid/METGRID.TBL
echo "   Updating WRF run/"
cp $WRF_PATH/WRF/run/* jan00-diagnostics/.
rm jan00-diagnostics/run_mpi jan00-diagnostics/run_real

# Update jan00-nesting
echo "Updating inputs for jan00-nesting"
echo "   Updating GEOGRID.TBL"
cp $WRF_PATH/WPS/geogrid/GEOGRID.TBL.ARW jan00-nesting/geogrid/GEOGRID.TBL
echo "   Updating METGRID.TBL"
cp $WRF_PATH/WPS/metgrid/METGRID.TBL.ARW jan00-nesting/metgrid/METGRID.TBL
echo "   Updating WRF run/"
cp $WRF_PATH/WRF/run/* jan00-nesting/.
rm jan00-nesting/run_mpi jan00-nesting/run_real

# Update jan00-quilting
echo "Updating inputs for jan00-quilting"
echo "   Updating GEOGRID.TBL"
cp $WRF_PATH/WPS/geogrid/GEOGRID.TBL.ARW jan00-quilting/geogrid/GEOGRID.TBL
echo "   Updating METGRID.TBL"
cp $WRF_PATH/WPS/metgrid/METGRID.TBL.ARW jan00-quilting/metgrid/METGRID.TBL
echo "   Updating WRF run/"
cp $WRF_PATH/WRF/run/* jan00-quilting/.
rm jan00-quilting/run_mpi jan00-quilting/run_real

# Update jan00-restart
echo "Updating inputs for jan00-restart"
echo "   Updating GEOGRID.TBL"
cp $WRF_PATH/WPS/geogrid/GEOGRID.TBL.ARW jan00-restart/geogrid/GEOGRID.TBL
echo "   Updating METGRID.TBL"
cp $WRF_PATH/WPS/metgrid/METGRID.TBL.ARW jan00-restart/metgrid/METGRID.TBL
echo "   Updating WRF run/"
cp $WRF_PATH/WRF/run/* jan00-restart/.
rm jan00-restart/run_mpi jan00-restart/run_real

