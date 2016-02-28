#!/bin/bash

# examples:
# ./release.sh 3.0.1 3.0.2 (standard usage)
# ./release.sh 3.0.0.01 3.0.0 (during our "milestone" mode, to use 3.0.0.qualifier again)

# if anything goes wrong, don't forget to delete the created tag locally, and to discard the last commit(s)

# useful for testing
mvnopts=""

CALL_DIR=`pwd`
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
REPO_DIR="$SCRIPT_DIR/.."

RELEASE_REPO_DIR="$CALL_DIR/.release-build"
RELEASE_BUILD_DIR="$RELEASE_REPO_DIR/org.moreunit.build"
WIP_COMMENT="Work in progress"

BRANCH=$(git branch --no-color | awk '$1=="*" {print $2}')
ORIGIN=$(git remote -v | awk '$1=="origin" && $3=="(push)" {print $2}')
MVN_PROFIL=release

function notify_user {
  echo
  echo "$1"
  if [ ! -z `which growlnotify` ]; then
    growlnotify `basename $0` -m "$1"
  fi
}
 
function failure {
  notify_user "$1"
  exit 1
}

function success {
  notify_user "$1"
  exit 0
}

function print_usage_and_exit {
  echo "Usage: release.sh version_to_release next_development_version"
  echo "   (or simply run release.sh and let you be guided by the script)"
  exit 1
}

function stash_work_in_progress {
  echo "Stashing work in progress..."
  git add -A
  git ls-files --deleted | xargs -I % git rm %
  git commit -m "$WIP_COMMENT"
  echo "Work in progress stashed."
  echo
}

function restore_work_in_progress {
  git log -n 1 | grep -q -c "$WIP_COMMENT" && echo "Restoring work in progress..." && git reset HEAD~1 && echo "Work in progress restored." && echo
}

function update_local_repository {
  echo "Updating local repository..."

  git fetch
  if [ "$?" -ne 0 ]
  then
    failure "Unable to fetch. Release aborted."
  fi

  stash_work_in_progress
  git rebase origin/${BRANCH}

  if [ "$?" -ne 0 ]
  then
    git rebase --abort
    restore_work_in_progress
    failure "Unable to rebase. Please pull or rebase and fix conflicts manually."
  fi
  restore_work_in_progress
}

# $1: NEW_VERSION, $2 (optional, constant): "SNAPSHOT"
function set_version {
  local NEW_VERSION="$1"
  local SNAPSHOT=""
  local QUALIFIER=""
  if [ "$2" == "SNAPSHOT" ]; then
    SNAPSHOT="-SNAPSHOT"
    QUALIFIER=".qualifier"
  fi

  local CATEGORY_FILE=../org.moreunit.updatesite/category.xml
  local MOCK_FEATURE_FILE=../org.moreunit.mock.feature/feature.xml

  mvn org.eclipse.tycho:tycho-versions-plugin:set-version $mvnopts -DnewVersion=${NEW_VERSION}${SNAPSHOT}
  if [ $? -ne 0 ]; then
    failure "Unable to set version to ${NEW_VERSION}${SNAPSHOT}. Release aborted."
  fi

  sed -i.bak "s/_[0-9\\.]\{1,\}\(.qualifier\)\{0,1\}.jar/_${NEW_VERSION}${QUALIFIER}.jar/g" "$CATEGORY_FILE"
  if [ $? -ne 0 ]; then
    failure "Unable to set version to ${NEW_VERSION}${QUALIFIER}. Release aborted."
  fi

  sed -i.bak "s/\" version=\"[^\"]\{1,\}\"/\" version=\"${NEW_VERSION}${QUALIFIER}\"/g" "$CATEGORY_FILE"
  if [ $? -ne 0 ]; then
    failure "Unable to set version to ${NEW_VERSION}${QUALIFIER}. Release aborted."
  fi

  # .qualifier versions are not supported when importing features, so let's just ignore it
  if [ -z "$SNAPSHOT" ]; then
    sed -i.bak "s/import feature=\"org.moreunit\" version=\"[^\"]\{1,\}\"/import feature=\"org.moreunit\" version=\"${NEW_VERSION}\"/g" "$MOCK_FEATURE_FILE"
    if [ $? -ne 0 ]; then
      failure "Unable to set version to ${NEW_VERSION}. Release aborted."
    fi
  fi

  rm -f "$CATEGORY_FILE.bak"
  rm -f "$MOCK_FEATURE_FILE.bak"
}

function zip_file_reminder {
  echo
  echo "** REMINDER *****************************************************"
  echo "* Don't forget to upload                                        *"
  echo "*     org.moreunit.updatesite/target/org.moreunit-VERSION.zip   *"
  echo "* on                                                            *"
  echo "*     https://sourceforge.net/projects/moreunit/files/moreunit/ *"
  echo "*****************************************************************"
  echo
}

version=$1
nextVersion=$2

if [ $# -ne 0 ]; then
  if [ -z "$version" -o  -z "$nextVersion" ]; then
    print_usage_and_exit
  fi
else
  echo -n "Please enter the version to release: "
  read version
  echo -n "Please enter next version to develop (without qualifier): "
  read nextVersion
fi

#if [ -z "$gitpwd" ]; then
#  echo -n "Please enter your GIT user password: "
#  read -s gitpwd
#  echo
#fi

cd "$REPO_DIR"

update_local_repository

rm -Rf "$RELEASE_REPO_DIR"
git clone -slb "${BRANCH}" "$REPO_DIR" "$RELEASE_REPO_DIR"
cd "$RELEASE_REPO_DIR"

cd "$RELEASE_BUILD_DIR"

set_version $version

cd "$RELEASE_REPO_DIR"

git ci -a -m "Release $version"
git tag -a "v$version" -m "Version $version"

cd "$RELEASE_BUILD_DIR"

mvn clean deploy $mvnopts -P$MVN_PROFIL
if [ $? -ne 0 ]; then
  failure "Build failed. Release aborted."
fi

# from this point, the release cannot be aborted anymore: the artifacts are on the update site! 

# first push, to ensure a consistent state between the remote repo and the update site
git push --tags $ORIGIN $BRANCH
if [ $? -ne 0 ]; then
  failure "Unable to push. Release aborted. THE NEWLY CREATED ARTIFACTS ARE ALREADY UPLOADED!"
fi

# first notification, in case of failure during the next steps
notify_user "Release successful!"
zip_file_reminder

notify_user "Preparing code for development on version $nextVersion..."
set_version $nextVersion 'SNAPSHOT'

cd "$RELEASE_REPO_DIR"

git ci -a -m "Prepares development on version $nextVersion"

git push $ORIGIN $BRANCH
if [ $? -ne 0 ]; then
  failure "Unable to push. Release aborted."
fi

notify_user "Code ready for development on version $nextVersion"

cd "$REPO_DIR" && git fetch
cd "$CALL_DIR"
# second notification, to be sure we don't forget about the zip file
zip_file_reminder
success "Version $version successfully released!"

