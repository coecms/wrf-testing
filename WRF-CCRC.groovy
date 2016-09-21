node ('saw562.raijin') {
    stage 'extract'
    sh 'rm -rf jenkins-tests'
    git changelog: false, poll: false, url: 'https://bitbucket.org/ScottWales/unsw-ccrc-wrf', branch: 'ccrc-3.7.1'
    sh 'git clone https://bitbucket.org/ScottWales/wrf-testing.git jenkins-tests'
    
    env.WRF_ROOT = pwd()
    
    stage 'compile_WRF'
    dir('WRFV3') {
        sh '''
module purge
module load intel-fc/15.0.3.187
module load intel-cc/15.0.3.187
module load openmpi/1.8.8
module load netcdf/4.3.3.1
setenv WRFIO_NCD_LARGE_FILE_SUPPORT 1
./configure << EOF
3
1
EOF'''
        sh 'qsub -W block=true run_compile'
    }
    
    stage 'compile_WPS'
    dir('WPS') {
        sh '''
module purge
module load intel-fc/15.0.3.187
module load intel-cc/15.0.3.187
module load openmpi/1.8.8
module load netcdf/4.3.3.1
setenv WRFIO_NCD_LARGE_FILE_SUPPORT 1
./configure << EOF
3
EOF'''
        sh 'qsub -W block=true run_compile'
    }
    
    dir('jenkins-tests'){
        dir('jan00'){
            stage 'jan00'
            sh 'qsub -W block=true -v PROJECT,WRF_ROOT runtest.sh'
            sh 'module load cdo; cdo diff wrfout_d01_2000-01-24_12\\:00\\:00 /projects/WRF/data/KGO/3.7.1/jan00/wrfout_d01_2000-01-24_12\\:00\\:00'
        }
        dir('jan00-nesting'){
            stage 'jan00-nesting'
            sh 'qsub -W block=true -v PROJECT,WRF_ROOT runtest.sh'
            sh 'module load cdo; cdo diff wrfout_d01_2000-01-24_12\\:00\\:00 /projects/WRF/data/KGO/3.7.1/jan00-nesting/wrfout_d01_2000-01-24_12\\:00\\:00'
            sh 'module load cdo; cdo diff wrfout_d02_2000-01-24_12\\:00\\:00 /projects/WRF/data/KGO/3.7.1/jan00-nesting/wrfout_d02_2000-01-24_12\\:00\\:00'
        }
    }
    
}
