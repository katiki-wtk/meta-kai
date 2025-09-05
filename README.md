# Préparation de l'image pour raspberrypi4-64

## Objectif:

* Image pour rpi4-64 et support applciations Flutter
* Support Wifi si branché sur Internet-Box


## Préparation build yocto

Adapter les répertoires à votre setup

    mkdir -p /yocto/rpi4-flutter/layers
    cd /yocto/rpi4-flutter/layers

    git clone -b scarthgap git://git.yoctoproject.org/poky
    pushd poky
    git clone -b scarthgap git://git.yoctoproject.org/meta-raspberrypi
    git clone -b scarthgap git://git.openembedded.org/meta-openembedded
    popd

    git clone -b scarthgap https://github.com/kraj/meta-clang.git
    git clone -b scarthgap https://github.com/meta-flutter/meta-flutter.git
    git clone https://github.com/katiki-wtk/meta-kai.git

    cd /yocto/rpi4-flutter
    source layers/poky/oe-init-build-env

    bitbake-layers add-layer   ../layers/meta-openembedded/meta-oe  ../layers/meta-openembedded/meta-python   ../layers/meta-raspberrypi   ../layers/meta-clang   ../layers/meta-flutter ../layers/meta-flutter/meta-flutter-apps

**Vérifier dans conf/bblayers.conf**

    cat conf/local.conf

    POKY_BBLAYERS_CONF_VERSION = "2"

    BBPATH = "${TOPDIR}"
    BBFILES ?= ""

    BBLAYERS ?= " \
    /yocto/rpi4-flutter/layers/poky/meta \
    /yocto/rpi4-flutter/layers/poky/meta-poky \
    /yocto/rpi4-flutter/layers/poky/meta-yocto-bsp \
    /yocto/rpi4-flutter/layers/poky/meta-openembedded/meta-oe \
    /yocto/rpi4-flutter/layers/poky/meta-openembedded/meta-multimedia \
    /yocto/rpi4-flutter/layers/poky/meta-openembedded/meta-networking \
    /yocto/rpi4-flutter/layers/poky/meta-openembedded/meta-python \
    /yocto/rpi4-flutter/layers/poky/meta-raspberrypi \
    /yocto/rpi4-flutter/layers/meta-flutter \
    /yocto/rpi4-flutter/layers/meta-clang \
    /yocto/rpi4-flutter/layers/meta-flutter/meta-flutter-apps \
    /yocto/rpi4-flutter/layers/meta-kai \
    "



## Editer le local.conf

Ajuster les variables suivantes:

    MACHINE = "raspberrypi4-64"
    DL_DIR = "/yocto/downloads"

    # DRM/KMS + OpenGL + systemd (requis par flutter-pi)
    DISTRO_FEATURES:append = " systemd opengl usrmerge"
    DISTRO_FEATURES:remove = " sysvinit "

    # S'assurer que l'init et udev pointent bien sur systemd
    VIRTUAL-RUNTIME_init_manager = "systemd"
    VIRTUAL-RUNTIME_initscripts = "systemd-compat-units"
    VIRTUAL-RUNTIME_udev = "systemd"

    # IP/Port de la machine ou tournera le grpc_persons_server
    # Ca ira dans /etc/flutter_ffi_grpc/config.ini
    GRPC_HOST = "192.168.1.50"
    GRPC_PORT = "50051"
    GRPC_TLS  = "false"


## Lancer le build

    bitbake -k kai-image

**Récupérer l'image dans tmp/deploy/images/raspberrypi4-64/ selon le format voulu**

    lrwxrwxrwx 2 kai kai        52 Sep  4 19:45 kai-image-raspberrypi4-64.rootfs.ext3 -> kai-image-raspberrypi4-64.rootfs-20250904173951.ext3
    lrwxrwxrwx 2 kai kai        56 Sep  4 19:45 kai-image-raspberrypi4-64.rootfs.manifest -> kai-image-raspberrypi4-64.rootfs-20250904173951.manifest
    lrwxrwxrwx 2 kai kai        57 Sep  4 19:46 kai-image-raspberrypi4-64.rootfs.rpi-sdimg -> kai-image-raspberrypi4-64.rootfs-20250904173951.rpi-sdimg
    lrwxrwxrwx 2 kai kai        60 Sep  4 19:45 kai-image-raspberrypi4-64.rootfs.spdx.tar.zst -> kai-image-raspberrypi4-64.rootfs-20250904173951.spdx.tar.zst
    lrwxrwxrwx 2 kai kai        55 Sep  4 19:45 kai-image-raspberrypi4-64.rootfs.tar.bz2 -> kai-image-raspberrypi4-64.rootfs-20250904173951.tar.bz2
    lrwxrwxrwx 2 kai kai        61 Sep  4 19:45 kai-image-raspberrypi4-64.rootfs.testdata.json -> kai-image-raspberrypi4-64.rootfs-20250904173951.testdata.json
    lrwxrwxrwx 2 kai kai        56 Sep  4 19:46 kai-image-raspberrypi4-64.rootfs.wic.bmap -> kai-image-raspberrypi4-64.rootfs-20250904173951.wic.bmap
    lrwxrwxrwx 2 kai kai        55 Sep  4 19:46 kai-image-raspberrypi4-64.rootfs.wic.bz2 -> kai-image-raspberrypi4-64.rootfs-20250904173951.wic.bz2


## Lancer l'application sur la cible

     /usr/bin/flutter-pi --release /usr/share/flutter/flutter_ffi_grpc/3.32.5/release/

**Vérfier les paramètres**

    root@raspberrypi4-64:~# cat /etc/flutter-ffi-grpc/config.ini
    [grpc]
    host = 192.168.1.50
    port = 50051
    tls  = false

**Si besoin de changer les paramètres Wifi:**

    root@raspberrypi4-64:~# cat /etc/wpa_supplicant/wpa_supplicant-wlan0.conf

    ctrl_interface=/var/run/wpa_supplicant
    update_config=1
    country=FR

    network={
        ssid="XXXXXXXXX"
        psk="ZZZZZZZZ"
        key_mgmt=WPA-PSK
        #scan_ssid=1
    }


