node ('saw562.raijin') {
    stage 'extract'
    sh 'rm -rf tests'
    if (params.VERSION == "3.9") {
        git changelog: false, poll: false, url: '/projects/WRF/WRFV_3.9'
    } else {
        git changelog: false, poll: false, url: '/projects/WRF/WRFV_3.7.1'
    }
    sh 'git clone https://bitbucket.org/ccarouge/wrf-testing.git tests'

    currentBuild.displayName += ' ' + params.VERSION
    env.WRF_ROOT = pwd()

    stage 'clean_WRF' 
    dir('WRFV3') {
       if (params.CLEAN_WRF == true) {
           sh './clean -a'
       }
       sh 'qsub -W umask=0022 -W block=true -q express -l walltime=1:00 -- sleep 1'
    }
    
    stage 'clean_WPS' 
    dir('WPS') {
       if (params.CLEAN_WPS == true) {
           sh './clean -a'
       }
       sh 'qsub -W umask=0022 -W block=true -q express -l walltime=1:00 -- sleep 1'
    }

    stage 'compile_WRF'
    dir('WRFV3') {
        sh '''./run_compile > configure.log << EOF
3
1
EOF'''
        sh 'qsub -W umask=0022 -W depend=afterany:$(tail -n 1 configure.log) -W block=true -q express -l walltime=1:00 -- sleep 1'
    }

    stage 'compile_WPS'
    dir('WPS') {
        sh '''./run_compile > configure.log << EOF
3
1
EOF'''
        sh 'qsub -W umask=0022 -W depend=afterany:$(tail -n 1 configure.log) -W block=true -q express -l walltime=1:00 -- sleep 1'
    }

    dir('tests'){
        dir('jan00'){
            stage 'jan00'
            sh 'qsub -W umask=0022 -W block=true -v PROJECT,WRF_ROOT runtest.sh'
            sh 'module load cdo; cdo diffn wrfout_d01_2000-01-24_12\\:00\\:00 /projects/WRF/data/KGO/3.7.1/jan00/wrfout_d01_2000-01-24_12\\:00\\:00'
        }
        dir('jan00-nesting'){
            stage 'jan00-nesting'
            sh 'qsub -W umask=0022 -W block=true -v PROJECT,WRF_ROOT runtest.sh'
            sh 'module load cdo; cdo diffn wrfout_d01_2000-01-24_12\\:00\\:00 /projects/WRF/data/KGO/3.7.1/jan00-nesting/wrfout_d01_2000-01-24_12\\:00\\:00'
            sh 'module load cdo; cdo diffn wrfout_d02_2000-01-24_12\\:00\\:00 /projects/WRF/data/KGO/3.7.1/jan00-nesting/wrfout_d02_2000-01-24_12\\:00\\:00'
        }
        dir('jan00-diagnostics'){
            stage 'jan00-diagnostics'
            sh 'qsub -W umask=0022 -W block=true -v PROJECT,WRF_ROOT runtest.sh'
            sh 'module load cdo; for file in wrfxtrm_d*_2000-01-24_12\\:00\\:00; do cdo diffn $file /projects/WRF/data/KGO/3.7.1/jan00-diagnostics/$file; done'
        }
    }

}
