#!/bin/bash

# # integ default
export SSH_TARGET="megalis@10.241.0.73"
export DEPLOY_PATH="/data/livraison/deploy.sh"


# develop || master -> preprod
if [ "$CI_COMMIT_REF_NAME" = "develop" ] || [ "$CI_COMMIT_REF_NAME" = "master" ]; then
  export SSH_TARGET="megalis@10.253.104.100"
  export DEPLOY_PATH="/data/livraison/deploy.sh"
fi


# prod
if [ "vide${CI_COMMIT_TAG}" != "vide" ]; then
  export SSH_TARGET="megalis@10.253.104.104"
  export DEPLOY_PATH="/data/livraison/deploy.sh"
fi

echo "serveur: `uname -a`"
echo "user: `id`"
echo "CI_COMMIT_REF_NAME: $CI_COMMIT_REF_NAME"
echo "SSH_TARGET: $SSH_TARGET"
echo "DEPLOY_PATH $DEPLOY_PATH"