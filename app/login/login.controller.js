LoginCtrl.$inject = ['$rootScope', '$scope', '$timeout', '$log', '$location', '$state', '$stateParams', 'restApi'];

export default function LoginCtrl($rootScope, $scope, $timeout, $log, $location, $state, $stateParams, restApi) {
    $scope.loading = false;
    $scope.authenticationFailed = false;
    $rootScope.title = "ProaSense";

    $scope.logIn = function () {
        $scope.authenticationFailed = false;
        $scope.loading = true;
        if ($stateParams.target != "") {
            restApi.loginSso($scope.credentials, $stateParams.target, $stateParams.session)
                .then(
                    function (response) {
                        $scope.loading = false;
                        if (response.data.success) {
                            {
                                $rootScope.username = response.data.info.authc.principal.username;
                                $rootScope.email = response.data.info.authc.principal.email;
                                $rootScope.token = response.data.token;
                                $rootScope.authenticated = true;
                                if ($stateParams.target != "") {
                                    console.log("going to " + $stateParams.target);
                                    $state.go($stateParams.target);
                                }
                            }
                        }
                    },
                    function (response) { // error
                        console.log(response);
                        $scope.loading = false;
                        $rootScope.authenticated = false;
                        $scope.authenticationFailed = true;
                    }
                );
        }
        //$http.post("/semantic-epa-backend/api/v2/admin/login", $scope.credentials)
        restApi.login($scope.credentials)
            .then(
                function (response) { // success
                    $scope.loading = false;
                    if (response.data.success) {
                        $rootScope.username = response.data.info.authc.principal.username;
                        $rootScope.email = response.data.info.authc.principal.email;
                        console.log(response.data.token);
                        $rootScope.token = response.data.token;
                        $rootScope.authenticated = true;
                        //if ($stateParams.target != "") {
                        //    console.log("going to " + $stateParams.target);
                        //    $state.go($stateParams.target);
                        //}
                        if ($rootScope.appConfig == 'ProaSense') $state.go("home");
                        else $state.go("streampipes");
                    }
                    else {
                        $rootScope.authenticated = false;
                        $scope.authenticationFailed = true;
                    }

                }, function (response) { // error
                    console.log(response);
                    $scope.loading = false;
                    $rootScope.authenticated = false;
                    $scope.authenticationFailed = true;
                }
            )
    };
}
;
