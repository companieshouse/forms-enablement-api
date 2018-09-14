MVN_OPTIONS ?=

all: dist

clean:
	mvn $(MVN_OPTIONS) clean

test-unit:
	mvn $(MVN_OPTIONS) test

test-int:
	mvn $(MVN_OPTIONS) verify

test: test-unit test-int

build:
	mvn $(MVN_OPTIONS) build

package:
	mvn $(MVN_OPTIONS) package
	@test -s ./target/formsapiservice*.jar || { echo "ERROR: Service JAR not found"; exit 1; }
	$(eval commit := $(shell git rev-parse --short HEAD))
	$(eval tag := $(shell git tag -l 'v*-rc*' --points-at HEAD))
	$(eval VERSION := $(shell if [[ -n "$(tag)" ]]; then echo $(tag) | sed 's/^v//'; else echo $(commit); fi))
	$(eval tmpdir:=$(shell mktemp -d build-XXXXXXXXXX))
	cp ./target/formsapiservice*.jar $(tmpdir)/forms-enablement-api.jar
	cp ./configuration.yml $(tmpdir)
	cp ./start.sh $(tmpdir)
	cd $(tmpdir); zip -r ../forms-enablement-api-$(VERSION).zip *
	rm -rf $(tmpdir)

dist: build package

.PHONY: all build clean package dist test test-unit test-int
