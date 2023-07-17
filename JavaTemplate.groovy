pipeline 'JavaTemplate', {
  projectName = 'MFS'

  formalParameter 'ec_stagesToRun', {
    expansionDeferred = '1'
  }

  stage 'Release Readiness', {
    colorCode = '#289ce1'
    pipelineName = 'JavaTemplate'
    gate 'POST', {
      task 'Code Coverage > 75', {
        gateCondition = '''$[/javascript
        //myStageRuntime.tasks[\'Run Code Quality and Security Scan\'].job.getLastSonarMetrics.coverage]	> 75	]'''
        gateType = 'POST'
        subproject = 'MFS'
        taskType = 'CONDITIONAL'
      }

      task 'Check for Approval Please', {
        gateType = 'POST'
        notificationEnabled = '0'
        notificationTemplate = 'ec_default_gate_task_notification_template'
        subproject = 'MFS'
        taskType = 'APPROVAL'
        approver = [
          'admin',
        ]
      }
    }

    task 'Get Release Dependencies', {
      actualParameter = [
        'depPropPath': '/myPipelineRuntime',
        'jsonFilePath': '/tmp/release.json',
        'startingpath': '/myJob/loadedPropertySheet',
      ]
      subprocedure = 'JSON file to properties'
      subproject = 'MFS'
      taskType = 'PROCEDURE'
    }

    task 'Get Sonar Scan Results', {
      actualParameter = [
        'config': '/projects/Default/pluginConfigurations/sonar',
        'resultFormat': 'propertysheet',
        'resultSonarProperty': '/myJob/getLastSonarMetrics',
        'sonarMetricsComplexity': 'all',
        'sonarMetricsDocumentation': 'all',
        'sonarMetricsDuplications': 'all',
        'sonarMetricsIssues': 'all',
        'sonarMetricsMaintainability': 'all',
        'sonarMetricsMetrics': 'all',
        'sonarMetricsQualityGates': 'all',
        'sonarMetricsReliability': 'all',
        'sonarMetricsSecurity': 'all',
        'sonarMetricsTests': 'all',
        'sonarProjectKey': 'petclinic',
        'sonarProjectName': 'petclinic',
        'sonarProjectVersion': '2.2.0.BUILD-SNAPSHOT',
        'sonarTaskId': '',
        'sonarTimeout': '',
      ]
      subpluginKey = 'EC-SonarQube'
      subprocedure = 'Get Last SonarQube Metrics'
      taskType = 'PLUGIN'
    }

    task 'Get Nexus-IQ Results', {
      enabled = '0'
      subproject = 'MFS'
    }

    task 'Get Helix ITSM Change Ticket', {
      enabled = '0'
      subproject = 'MFS'
    }
  }

  stage 'Release', {
    colorCode = '#06b732'
    pipelineName = 'JavaTemplate'
    task 'Deploy Microservice A', {
      actualParameter = [
        'APP_NAME': 'microserviceA',
      ]
      ciConfigurationName = 'ikurtz-cbdemos-thunder'
      ciJobFolder = '/'
      ciJobName = 'deploy-microservice'
      condition = '$[/javascript myPipelineRuntime.RORelations == "microserviceA"]'
      subproject = 'MFS'
      taskType = 'CI_JOB'
    }

    task 'Deploy Microservice B', {
      actualParameter = [
        'APP_NAME': 'microserviceA',
      ]
      ciConfigurationName = 'ikurtz-cbdemos-thunder'
      ciJobFolder = '/'
      ciJobName = 'deploy-microservice'
      condition = '$[/javascript myPipelineRuntime.RORelations == "microserviceA,microserviceB"]'
      subproject = 'MFS'
      taskType = 'CI_JOB'
    }

    task 'Deploy Microservice C', {
      actualParameter = [
        'APP_NAME': 'microserviceA',
      ]
      ciConfigurationName = 'ikurtz-cbdemos-thunder'
      ciJobFolder = '/'
      ciJobName = 'deploy-microservice'
      condition = '$[/javascript myPipelineRuntime.RORelations == "microserviceC"]'
      subproject = 'MFS'
      taskType = 'CI_JOB'
    }

    task 'Deploy Sample Insurance Application', {
      ciConfigurationName = 'ikurtz-cbdemos-thunder'
      ciJobBranchName = 'main'
      ciJobFolder = '/'
      ciJobName = 'Insurance Frontend'
      subproject = 'MFS'
      taskType = 'CI_JOB'
    }

    task 'Deploy Validation - Run Smoke Tests', {
      subprocedure = 'Smoke Test'
      subproject = 'MFS'
      taskType = 'PROCEDURE'
    }
  }

  // Custom properties

  property 'ec_counters', {

    // Custom properties
    pipelineCounter = '1'
  }
}