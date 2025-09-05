SUMMARY = "Bibliothèque C++ FFI (libpersons.so)"
LICENSE = "CLOSED"

SRCREV = "67fb61d05928de220303a123cc6f6b395fc0fafa"
#gSRC_URI = "https://bitbucket.org/adeneo-embedded/flutter_ffi_grpc/get/${SRCREV}.tar.gz;downloadfilename=flutter_ffi_grpc-${SRCREV}.tar.gz;subdir=src"
SRC_URI = "git://github.com/katiki-wtk/flutter_ffi_grpc.git;protocol=https;branch=master"



PV = "1.0+git${SRCPV}"

# Les sources C/C++ sont sous native/
S = "${WORKDIR}/git/native"

CXXFLAGS:append = " -std=c++17 "

do_compile() {
    # Construire la lib partagée
    ${CXX} ${CXXFLAGS} -fPIC -shared \
        -std=c++17 \
        persons.cpp persons_c_ffi.cpp \
        -Wl,-soname,libpersons.so.1 \
        -o libpersons.so.1.0 ${LDFLAGS}
}

do_install() {
    install -d ${D}${libdir}

    install -m 0755 libpersons.so.1.0 ${D}${libdir}/libpersons.so.1.0
    ln -sf libpersons.so.1.0 ${D}${libdir}/libpersons.so.1
    ln -sf libpersons.so.1   ${D}${libdir}/libpersons.so
}

PACKAGES = "${PN} ${PN}-dev ${PN}-dbg"
FILES:${PN} = "${libdir}/libpersons.so.*"
FILES:${PN} += "${libdir}/libpersons.so"
FILES:${PN}-dev   += "${libdir}/libpersons.so"

INSANE_SKIP:${PN} += "dev-so"



