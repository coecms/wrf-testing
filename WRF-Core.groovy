node ('ccc561.gadi') {
    stage 'extract'
    // Get the tests. Clone the wrf-testing repository again in tests/
    // Then checkout the branch for the tested version as indicated in params.VERSION
    sh 'rm -rf tests'
    git branch: "V${params.VERSION}", changelog: false, poll: false, url: 'https://github.com/coecms/WRF.git'
    git submodule update --init --recursive
    // git changelog: false, poll: false, url: "/projects/WRF/WRFV_${params.VERSION}"
    sh 'git clone https://github.com/coecms/wrf-testing.git tests'
    dir('tests') {
       sh "git branch --track 4.4 origin/4.4"
       sh "git checkout 4.4"  
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
        dir('oct16'){
            if (params.OCT16 == true) {
            	stage 'oct16'
           	    sh 'qsub -W block=true -v PROJECT,WRF_ROOT runtest.sh'
            	sh "module load cdo; cdo diffn wrfout_d01_2016-10-06_00\\:00\\:00 /g/data/sx70/data/KGO/${params.VERSION}/oct16/wrfout_d01_2016-10-06_00\\:00\\:00"
            }
	    }
        dir('oct16-nesting'){
            if (params.NESTING == true) {
                stage 'oct16-nesting'
                sh 'qsub -W block=true -v PROJECT,WRF_ROOT runtest.sh'
                sh "module load cdo; cdo diffn wrfout_d01_2016-10-06_00\\:00\\:00 /g/data/sx70/data/KGO/${params.VERSION}/oct16-nesting/wrfout_d01_2016-10-06_00\\:00\\:00"
                sh "module load cdo; cdo diffn wrfout_d02_2016-10-06_00\\:00\\:00 /g/data/sx70/data/KGO/${params.VERSION}/oct16-nesting/wrfout_d02_2016-10-06_00\\:00\\:00"
            }
        }
        dir('oct16-diagnostics'){
            if (params.DIAG == true) {
                stage 'oct16-diagnostics'
                sh 'qsub -W block=true -v PROJECT,WRF_ROOT runtest.sh'
                sh "module load cdo; for file in wrfxtrm_d*_2016-10-06_00\\:00\\:00; do cdo diffn \$file /g/data/sx70/data/KGO/${params.VERSION}/oct16-diagnostics/\$file; done"
            }
        }
        // dir('oct16-quilting'){
        //     if (params.QUILTING == true) {
        //         stage 'oct16-quilting'
        //         sh 'cp ../../WRF/run/* ../../WPS/run_WPS.sh .'
        //         sh 'cp namelists/namelist.wps namelist.wps'
        //         sh 'cp namelists/namelist.input-quilting namelist.input'
        //         sh 'qsub -W block=true -v PROJECT,WRF_ROOT run_WPS.sh'
        //         sh 'qsub -W block=true -v PROJECT,WRF_ROOT run_real'
        //         sh 'qsub -W block=true -v PROJECT,WRF_ROOT run_mpi'
        //         sh "module load cdo; cdo diffn wrfout_d01_2016-10-06_00\\:00\\:00 /g/data/sx70/data/KGO/${params.VERSION}/oct16/wrfout_d01_2016-10-06_00\\:00\\:00"
        //     }
        // }
        dir('oct16-restart'){
            if (params.RESTART == true) {
                stage 'oct16-restart'
                sh 'qsub -W block=true -v PROJECT,WRF_ROOT runtest.sh'
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
