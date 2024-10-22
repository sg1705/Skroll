module.exports = function() {
    var client = './';
    //var client = './src/client/';
    var server = './src/server/';
    var clientApp = client + 'scripts/';
    var report = './report/';
    var root = './';
    var specRunnerFile = 'specs.html';
    var temp = './.tmp/';
    var wiredep = require('wiredep');
    var bowerFiles = wiredep({devDependencies: true})['js'];
    var bower = {
        json: require('./bower.json'),
        directory: './bower_components/',
        ignorePath: '../..'
    };
    var nodeModules = 'node_modules';

    var config = {
        /**
         * File paths
         */
        // all javascript that we want to vet
        alljs: [
            './src/**/*.js',
            './*.js'
        ],
        build: './build/',
        client: client,
        css: temp + 'styles.css',
        fonts: bower.directory + 'font-awesome/fonts/**/*.*',
        html: client + '**/*.html',
        htmltemplates: clientApp + '**/*.html',
        images: client + 'images/**/*.*',
        other: client + 'other/**/*',
        index: client + 'index.html',
        iframe: client + 'iframe.html',
        pdf: client + 'pdf',
        // app js, with no specs
        js: [
            clientApp + '**/*.module.js',
            clientApp + '**/core.config/*.js',
            clientApp + '**/*.js',
            '!' + clientApp + '**/*.spec.js',
            '!' + clientApp + '**/upload/jquery.fileupload.js', //@todo: remove this later
            '!' + clientApp + '**/upload/skFileUpload.js' //@todo: remove this later

        ],
        jsOrder: [
            '**/app.module.js',
            '**/*.module.js',
            '**/*.js'
        ],
        less: client + 'styles/*.less',
        sass: [
            client + 'styles/*.scss',
            clientApp + '**/*.scss',
        ],
        report: report,
        root: root,
        server: server,
        source: 'src/',
        stubsjs: [
            bower.directory + 'angular-mocks/angular-mocks.js',
            client + 'stubs/**/*.js'
        ],
        temp: temp,

        /**
         * optimized files
         */
        optimized: {
            app: 'app.js',
            lib: 'lib.js'
        },

        /**
         * plato
         */
        plato: {js: clientApp + '**/*.js'},

        /**
         * browser sync
         */
        browserReloadDelay: 1000,

        /**
         * template cache
         */
        templateCache: {
            file: 'templates.js',
            options: {
                module: 'app.core',
                root: 'scripts/',
                standalone: false
            }
        },

        /**
         * Bower and NPM files
         */
        bower: bower,
        packages: [
            './package.json',
            './bower.json'
        ],

        /**
         * specs.html, our HTML spec runner
         */
        specRunner: client + specRunnerFile,
        specRunnerFile: specRunnerFile,

        /**
         * The sequence of the injections into specs.html:
         *  1 testlibraries
         *      mocha setup
         *  2 bower
         *  3 js
         *  4 spechelpers
         *  5 specs
         *  6 templates
         */
        testlibraries: [
            nodeModules + '/mocha/mocha.js',
            nodeModules + '/chai/chai.js',
            nodeModules + '/sinon-chai/lib/sinon-chai.js'
        ],
        specHelpers: [client + 'test-helpers/*.js'],
        specs: [clientApp + '**/*.spec.js'],
        serverIntegrationSpecs: [client + '/tests/server-integration/**/*.spec.js'],

        /**
         * Node settings
         */
        nodeServer: server + 'app.js',
        defaultPort: '8001'
    };

    /**
     * wiredep and bower settings
     */
    config.getWiredepDefaultOptions = function() {
        var options = {
            bowerJson: config.bower.json,
            directory: config.bower.directory,
            ignorePath: config.bower.ignorePath,
            exclude: [
                    'bower_components/pdf.js-viewer/pdf.js',
                    'bower_components/angular-feature-flags/dist/featureFlags.js',
                    'bower_components/angulartics/src/angulartics-clicky.js',
                    'bower_components/angulartics/src/angulartics-cnzz.js',
                    'bower_components/angulartics/src/angulartics-ga-cordova.js',
                    'bower_components/angulartics/src/angulartics-gtm.js',
                    'bower_components/angulartics/src/angulartics-piwik.js',
                    'bower_components/angulartics/src/angulartics-scroll.js',
                    'bower_components/angulartics/src/angulartics-splunk.js',
                    'bower_components/angulartics/src/angulartics-woopra.js',
                    'bower_components/angulartics/src/angulartics-marketo.js',
                    'bower_components/angulartics/src/angulartics-intercom.js',
                    'bower_components/angulartics/src/angulartics-inspectlet.js',
                    'bower_components/angulartics/src/angulartics-newrelic-insights.js',
                    'bower_components/rangy/rangy-classapplier.js',
                    'bower_components/rangy/rangy-highlighter.js',
                    'bower_components/rangy/rangy-selectionsaverestore.js']

        };
        return options;
    };

    /**
     * karma settings
     */
    config.karma = getKarmaOptions();

    return config;

    ////////////////

    function getKarmaOptions() {
        var options = {
            files: [].concat(
                bowerFiles,
                config.specHelpers,
                clientApp + '**/*.module.js',
                clientApp + '**/*.js',
                temp + config.templateCache.file,
                config.serverIntegrationSpecs
            ),
            exclude: [],
            coverage: {
                dir: report + 'coverage',
                reporters: [
                    // reporters not supporting the `file` property
                    {type: 'html', subdir: 'report-html'},
                    {type: 'lcov', subdir: 'report-lcov'},
                    {type: 'text-summary'} //, subdir: '.', file: 'text-summary.txt'}
                ]
            },
            preprocessors: {}
        };
        options.preprocessors[clientApp + '**/!(*.spec)+(.js)'] = ['coverage'];
        return options;
    }
};
