APP_JAR=$(echo *.jar)

if [ ! -f "${APP_JAR}" ]
then
    echo "ERROR: Service JAR not found"
    exit 1
fi

if [ -z ${MESOS_SLAVE_PID+x} ]
then
    java -jar $APP_JAR server configuration.yml
    exit $?
fi

set -x

PORT=$1
CONFIG_URL=$2
ENVIRONMENT=$3
APP_NAME=$4

export APP_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

source /etc/profile

# Download config file
echo "Downloading environment from: $CONFIG_URL/$ENVIRONMENT/$APP_NAME"
wget -O $APP_DIR/private_env $CONFIG_URL/$ENVIRONMENT/private_env
wget -O $APP_DIR/global_env $CONFIG_URL/$ENVIRONMENT/global_env
wget -O $APP_DIR/app_env $CONFIG_URL/$ENVIRONMENT/${APP_NAME}/env
source $APP_DIR/private_env
source $APP_DIR/global_env
source $APP_DIR/app_env

java -Xmx800m -jar $APP_JAR server configuration.yml > forms-enablement-api.log 2>&1
