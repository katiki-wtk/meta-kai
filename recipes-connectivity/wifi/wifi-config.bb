SUMMARY = "Préconfig Wi-Fi (wpa_supplicant + DHCP via udhcpc)"
DESCRIPTION = "Installe wpa_supplicant.conf pour wlan0 et un service systemd udhcpc pour obtenir une IP."
LICENSE = "CLOSED"

inherit systemd

SRC_URI = "file://wpa_supplicant-wlan0.conf.in \
           file://wlan0-dhcp.service \
"

S = "${WORKDIR}"

# Variables injectées depuis local.conf (ou un fichier inclus) :
# WIFI_COUNTRY = "FR"
# WIFI_SSID    = "TonSSID"
# WIFI_PSK     = "TonMotDePasse"  (ou PSK hex sans guillemets si le template le prévoit)
WIFI_COUNTRY ?= "FR"
WIFI_SSID ?= "Livebox-1060"
WIFI_PSK  ?= "57g5dG3y7UYsfJ2ReT"
#WIFI_SSID ?= "Livebox-9130"
#WIFI_PSK  ?= "xARzESDMgdkSyijQWj"


do_install() {
    # wpa_supplicant config
    install -d ${D}${sysconfdir}/wpa_supplicant
    sed -e "s/@COUNTRY@/${WIFI_COUNTRY}/" \
        -e "s/@SSID@/${WIFI_SSID}/" \
        -e "s/@PSK@/${WIFI_PSK}/" \
        ${WORKDIR}/wpa_supplicant-wlan0.conf.in \
        > ${D}${sysconfdir}/wpa_supplicant/wpa_supplicant-wlan0.conf
    chmod 600 ${D}${sysconfdir}/wpa_supplicant/wpa_supplicant-wlan0.conf

    # Unité systemd pour le DHCP via udhcpc (busybox)
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/wlan0-dhcp.service ${D}${systemd_system_unitdir}/
}

# On ne référence ici QUE les unités installées par cette recette
SYSTEMD_SERVICE:${PN} = "wlan0-dhcp.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

# Dépendances runtime minimales : supplicant + udhcpc (busybox)
RDEPENDS:${PN} = "wpa-supplicant busybox busybox-udhcpc "

# Activer l'instance wpa_supplicant@wlan0 au 1er boot (sur la cible)
pkg_postinst:${PN} () {
    if [ -z "$D" ] && command -v systemctl >/dev/null 2>&1; then
        systemctl enable wpa_supplicant@wlan0.service || true
        systemctl restart wpa_supplicant@wlan0.service || true
    fi
}


