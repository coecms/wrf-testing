node ('ccc561.gadi') {
    stage 'extract'
    // Get the tests. Clone the wrf-testing repository again in tests/
    // Then checkout the branch for the tested version as indicated in params.VERSION
    sh 'rm -rf tests'
    git branch: "V${params.VERSION}", changelog: false, poll: false, url: 'https://github.com/coecms/WRF.git'
    // git changelog: false, poll: false, url: "/projects/WRF/WRFV_${params.VERSION}"
    sh 'git clone https://github.com/coecms/wrf-testing.git tests'
    dir('tests') {
       sh "git branch --track 4.2.2 origin/4.2.2"
       sh "git checkout 4.2.2"  
    }

    currentBuild.displayName += ' ' + params.VERSION
    env.WRF_ROOT = pwd()
    env.KGO_ROOT = '/g/data/sx70/data/KGO/${params.VERSION}'

    stage 'clean_WRF' 
    // Clean previous WRF compilation if wanting to start from scratch (optional)
    dir('WRF') {
       if (params.CLEAN_WRF == true) {
           sh './clean -a'
       }
    }
    
    stage 'clean_WPS' 
    // Clean previous WPS compilation if wanting to start from scratch (optional)
    dir('WPS') {
       if (params.CLEAN_WPS == true) {
           sh './clean -a'
       }
    }

    stage 'compile_WRF'
    // Compile WRF
    dir('WRF') {
        sh './run_compile -t'
    }

    stage 'compile_WPS'
    // Compile WPS
    dir('WPS') {
        sh './run_compile -t'
    }

    stage 'compile_UPP'
    // Compile UPP
    //dir('UPPV3.2') {
    //    sh './run_compile -t'
    //}

    dir('tests'){
    // Start run tests.
        dir('jan00'){
            if (params.JAN00 == true) {
            	stage 'jan00'
            	sh 'cp ../../WRF/run/run_real ../../WRF/run/run_mpi ../../WPS/run_WPS.sh .'
            	sh 'cp ../../WPS/namelist.wps.jan00 namelist.wps'
            	sh 'cp ../../WRF/test/em_real/namelist.input.jan00 namelist.input'
            	sh 'qsub -W block=true -v PROJECT,WRF_ROOT run_WPS.sh'
            	sh 'qsub -W block=true -v PROJECT,WRF_ROOT run_real'
            	sh 'qsub -W block=true -v PROJECT,WRF_ROOT run_mpi'
            	sh "module load cdo; cdo diffn wrfout_d01_2000-01-24_12\\:00\\:00 /g/data/sx70/data/KGO/${params.VERSION}/jan00/wrfout_d01_2000-01-24_12\\:00\\:00"
            }
	    }
        dir('jan00-nesting'){
            if (params.NESTING == true) {
                stage 'jan00-nesting'
                sh 'cp ../../WRF/run/run_real ../../WRF/run/run_mpi ../../WPS/run_WPS.sh .'
                sh 'cp ../../WPS/namelist.wps.jan00-nesting namelist.wps'
                sh 'cp ../../WRF/test/em_real/namelist.input.jan00-nesting namelist.input'
                sh 'qsub -W block=true -v PROJECT,WRF_ROOT run_WPS.sh'
                sh 'qsub -W block=true -v PROJECT,WRF_ROOT run_real'
                sh 'qsub -W block=true -v PROJECT,WRF_ROOT run_mpi'
                sh "module load cdo; cdo diffn wrfout_d01_2000-01-24_12\\:00\\:00 /g/data/sx70/data/KGO/${params.VERSION}/jan00-nesting/wrfout_d01_2000-01-24_12\\:00\\:00"
                sh "module load cdo; cdo diffn wrfout_d02_2000-01-24_12\\:00\\:00 /g/data/sx70/data/KGO/${params.VERSION}/jan00-nesting/wrfout_d02_2000-01-24_12\\:00\\:00"
            }
        }
        dir('jan00-diagnostics'){
            if (params.DIAG == true) {
                stage 'jan00-diagnostics'
                sh 'cp ../../WRF/run/run_real ../../WRF/run/run_mpi ../../WPS/run_WPS.sh .'
                sh 'cp ../../WPS/namelist.wps.jan00-nesting namelist.wps'
                sh 'cp ../../WRF/test/em_real/namelist.input.jan00-diagnostics namelist.input'
                sh 'qsub -W block=true -v PROJECT,WRF_ROOT run_WPS.sh'
                sh 'qsub -W block=true -v PROJECT,WRF_ROOT run_real'
                sh 'qsub -W block=true -v PROJECT,WRF_ROOT run_mpi'
                sh "module load cdo; for file in wrfxtrm_d*_2000-01-24_12\\:00\\:00; do cdo diffn \$file /g/data/sx70/data/KGO/${params.VERSION}/jan00-diagnostics/\$file; done"
            }
        }
        dir('jan00-quilting'){
            if (params.QUILTING == true) {
                stage 'jan00-quilting'
                sh 'cp ../../WRF/run/run_real ../../WRF/run/run_mpi ../../WPS/run_WPS.sh .'
                sh 'cp ../../WPS/namelist.wps.jan00 namelist.wps'
                sh 'cp ../../WRF/test/em_real/namelist.input.jan00-quilting namelist.input'
                sh 'qsub -W block=true -v PROJECT,WRF_ROOT run_WPS.sh'
                sh 'qsub -W block=true -v PROJECT,WRF_ROOT run_real'
                sh 'qsub -W block=true -v PROJECT,WRF_ROOT run_mpi'
                sh "module load cdo; cdo diffn wrfout_d01_2000-01-24_12\\:00\\:00 /g/data/sx70/data/KGO/${params.VERSION}/jan00/wrfout_d01_2000-01-24_12\\:00\\:00"
            }
        }
        dir('jan00-restart'){
            if (params.RESTART == true) {
                stage 'jan00-restart'
                sh 'cp ../../WRF/run/run_real ../../WRF/run/run_mpi ../../WPS/run_WPS.sh .'
                sh 'cp ../../WPS/namelist.wps.jan00 namelist.wps'
                sh 'cp ../../WRF/test/em_real/namelist.input.jan00-restart* .'
                sh 'qsub -W block=true -v PROJECT,WRF_ROOT run_WPS.sh'
                sh 'qsub -W block=true -v PROJECT,WRF_ROOT run_real'
                sh 'cp namelist.input.jan00-restart1 namelist.input'
                sh 'qsub -W block=true -v PROJECT,WRF_ROOT run_mpi'
                sh 'cp namelist.input.jan00-restart2 namelist.input'
                sh 'qsub -W block=true -v PROJECT,WRF_ROOT run_mpi'
                sh './compare_output.sh'
            }
        }
	//dir('UPP'){
        //    dir('postprd'){
	//        stage 'UPP'
	//        sh './run_unipost_frames'
	//    }
        //}
    }

}
