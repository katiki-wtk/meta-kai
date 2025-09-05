SUMMARY = "Autoload RPi4 Wi-Fi brcmfmac"
LICENSE = "MIT"

RDEPENDS:${PN} = ""
S = "${WORKDIR}"

do_install() {
    install -d ${D}${sysconfdir}/modules-load.d
    echo "brcmfmac" > ${D}${sysconfdir}/modules-load.d/brcmfmac.conf
}

FILES:${PN} = "${sysconfdir}/modules-load.d/brcmfmac.conf"

