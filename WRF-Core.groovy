node ('saw562.raijin') {
    stage 'extract'
    sh 'rm -rf tests'
    git changelog: false, poll: false, url: "/projects/WRF/WRFV_${params.VERSION}"
    sh 'git clone https://github.com/coecms/wrf-testing.git tests'
    dir('tests') {
       sh "git branch --track ${params.VERSION} origin/${params.VERSION}"
       sh "git checkout ${params.VERSION}"  
    }

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
        sh 'qsub -W umask=0022 -W block=true -q express ./run_compile'
    }

    stage 'compile_WPS'
    dir('WPS') {
        sh 'qsub -W umask=0022 -W block=true -q express ./run_compile'
    }

    dir('tests'){
        dir('jan00'){
            stage 'jan00'
            sh 'qsub -W umask=0022 -W block=true -v PROJECT,WRF_ROOT runtest.sh'
            sh "module load cdo; cdo diffn wrfout_d01_2000-01-24_12\\:00\\:00 /projects/WRF/data/KGO/${params.VERSION}/jan00/wrfout_d01_2000-01-24_12\\:00\\:00"
        }
        dir('jan00-nesting'){
            stage 'jan00-nesting'
            sh 'qsub -W umask=0022 -W block=true -v PROJECT,WRF_ROOT runtest.sh'
            sh "module load cdo; cdo diffn wrfout_d01_2000-01-24_12\\:00\\:00 /projects/WRF/data/KGO/${params.VERSION}/jan00-nesting/wrfout_d01_2000-01-24_12\\:00\\:00"
            sh "module load cdo; cdo diffn wrfout_d02_2000-01-24_12\\:00\\:00 /projects/WRF/data/KGO/${params.VERSION}/jan00-nesting/wrfout_d02_2000-01-24_12\\:00\\:00"
        }
        dir('jan00-diagnostics'){
            stage 'jan00-diagnostics'
            sh 'qsub -W umask=0022 -W block=true -v PROJECT,WRF_ROOT runtest.sh'
            sh "module load cdo; for file in wrfxtrm_d*_2000-01-24_12\\:00\\:00; do cdo diffn \$file /projects/WRF/data/KGO/${params.VERSION}/jan00-diagnostics/\$file; done"
        }
    }

}
