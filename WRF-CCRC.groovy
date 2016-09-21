node ('saw562.raijin') {
    stage 'extract'
    sh 'rm -rf tests'
    git changelog: false, poll: false, url: 'https://bitbucket.org/ScottWales/unsw-ccrc-wrf', branch: 'ccrc-3.7.1'
    sh 'git clone https://bitbucket.org/ScottWales/wrf-testing.git tests'
    
    env.WRF_ROOT = pwd()
    
    stage 'compile_WRF'
    dir('WRFV3') {
        sh '''./run_compile > configure.log << EOF
3
1
EOF'''
        sh 'qsub -W depend=afterany:$(tail -n 1 configure.log) -W block=true -q express -l walltime=1:00 -- sleep 1'
    }
    
    stage 'compile_WPS'
    dir('WPS') {
        sh '''./run_compile > configure.log << EOF
3
1
EOF'''
        sh 'qsub -W depend=afterany:$(tail -n 1 configure.log) -W block=true -q express -l walltime=1:00 -- sleep 1'
    }
    
    dir('tests'){
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
