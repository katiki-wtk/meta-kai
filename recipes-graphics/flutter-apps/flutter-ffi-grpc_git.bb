SUMMARY = "Flutter app (FFI + gRPC) for RaspberryPi (flutter-pi)"
LICENSE = "CLOSED"

# ⚠️ Mets bien la branche qui existe dans ton repo (main OU master)
SRC_URI = "git://github.com/katiki-wtk/flutter_ffi_grpc.git;protocol=https;branch=master"
SRCREV  = "${AUTOREV}"

PV = "1.0+git${SRCPV}"

# Racine du projet (contient pubspec.yaml)
S = "${WORKDIR}/git"

# Doit correspondre à 'name:' dans pubspec.yaml
PUBSPEC_APPNAME = "flutter_ffi_grpc"

inherit flutter-app

# (Optionnel) patch de chemin dans le code – utiliser des " pour expandre la variable
do_configure:append() {
    sed -i "s|c:/tmp/people.csv|${datadir}/flutter/${PUBSPEC_APPNAME}/data/people.csv|g" \
        ${S}/lib/ffi_person_page.dart || true
}

do_install:append() {
    # 1) Données d’exemple
    install -d ${D}${datadir}/flutter/${PUBSPEC_APPNAME}/data
    echo "Karim,Atiki,29"   > ${D}${datadir}/flutter/${PUBSPEC_APPNAME}/data/people.csv
    echo "Line-Ce,Beni,27" >> ${D}${datadir}/flutter/${PUBSPEC_APPNAME}/data/people.csv
    echo "Zak,Harry,16"    >> ${D}${datadir}/flutter/${PUBSPEC_APPNAME}/data/people.csv
    echo "Hope,AB,19"      >> ${D}${datadir}/flutter/${PUBSPEC_APPNAME}/data/people.csv

    # 2) Lien 'current' vers la version installée par la classe flutter-app
    install -d ${D}${datadir}/flutter/${PUBSPEC_APPNAME}
    ln -sfn ${PV} ${D}${datadir}/flutter/${PUBSPEC_APPNAME}/current

    # 3) Wrapper /usr/bin/${PUBSPEC_APPNAME}
    install -d ${D}${bindir}
    cat > ${D}${bindir}/${PUBSPEC_APPNAME} <<EOF
#!/bin/sh
exec /usr/bin/flutter-pi --release ${datadir}/flutter/${PUBSPEC_APPNAME}/current/release/
EOF
    chmod 0755 ${D}${bindir}/${PUBSPEC_APPNAME}


     # Edit  /etc/flutter-ffi-grpc/config.ini 
    install -d ${D}${sysconfdir}/flutter-ffi-grpc
    cat > ${D}${sysconfdir}/flutter-ffi-grpc/config.ini <<EOF
[grpc]
host = ${GRPC_HOST}
port = ${GRPC_PORT}
tls  = ${GRPC_TLS}
# pem_path = ${GRPC_PEM_PATH}
EOF
    chmod 0644 ${D}${sysconfdir}/flutter-ffi-grpc/config.ini


    sed -i '1a export GRPC_CONFIG="/etc/flutter-ffi-grpc/config.ini"' \
        ${D}${bindir}/${PUBSPEC_APPNAME}
}

# Runtime: embedder + ta lib FFI
RDEPENDS:${PN} += "flutter-pi persons-ffi "
FILES:${PN} += " ${sysconfdir}/flutter-ffi-grpc ${sysconfdir}/flutter-ffi-grpc/config.ini"
CONFFILES:${PN} += "${sysconfdir}/flutter-ffi-grpc/config.ini"



