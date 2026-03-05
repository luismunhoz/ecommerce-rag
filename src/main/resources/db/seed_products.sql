-- =============================================================
-- Seed: Categories + 100 Products (Computer Equipment Store)
-- =============================================================

-- ---------------------------------------------------------------
-- CATEGORIES
-- ---------------------------------------------------------------
INSERT INTO categories (id, name, description, slug, parent_id) VALUES
(1,  'Laptops',        'Portable computers for work and gaming',              'laptops',        NULL),
(2,  'Desktops',       'High-performance desktop computers and workstations', 'desktops',       NULL),
(3,  'Monitors',       'Screens and displays of all sizes and resolutions',   'monitors',       NULL),
(4,  'Mice',           'Wired and wireless computer mice',                    'mice',           NULL),
(5,  'Keyboards',      'Mechanical, membrane, and wireless keyboards',        'keyboards',      NULL),
(6,  'Audio',          'Sound bars, headsets, and speakers',                  'audio',          NULL),
(7,  'Computer Desks', 'Gaming and office computer desks and furniture',      'computer-desks', NULL),
(8,  'Projectors',     'Business and home theater video projectors',          'projectors',     NULL),
(9,  'Networking',     'Routers, switches, and network accessories',          'networking',     NULL),
(10, 'Storage',        'SSDs, HDDs, and external storage devices',            'storage',        NULL);

-- Advance the sequence so future auto-inserts do not conflict
SELECT setval('categories_id_seq', 10, true);


-- ---------------------------------------------------------------
-- PRODUCTS
-- ---------------------------------------------------------------

-- LAPTOPS (15)
INSERT INTO products (name, description, price, stock_quantity, sku, image_url, category_id, active) VALUES
('Dell XPS 15 9530',           'Intel Core i7-13700H, 16 GB RAM, 512 GB SSD, 15.6" OLED touch, NVIDIA RTX 4060',                1849.99, 25, 'LAP-DELL-XPS15-001',    NULL, 1, true),
('Apple MacBook Pro 14" M3',   'Apple M3 chip, 18 GB unified memory, 512 GB SSD, Liquid Retina XDR display',                   1999.99, 20, 'LAP-APPL-MBP14-001',    NULL, 1, true),
('Lenovo ThinkPad X1 Carbon',  'Intel Core i7-1365U, 32 GB RAM, 1 TB SSD, 14" IPS, ultra-light business laptop',              1699.99, 18, 'LAP-LENX-X1C-001',      NULL, 1, true),
('ASUS ROG Zephyrus G14',      'AMD Ryzen 9 7940HS, 16 GB RAM, 1 TB SSD, 14" QHD 165 Hz, NVIDIA RTX 4060',                    1449.99, 22, 'LAP-ASUS-ROG14-001',    NULL, 1, true),
('HP Spectre x360 14',         'Intel Core i7-1355U, 16 GB RAM, 512 GB SSD, 14" OLED 2.8K touch, 2-in-1 convertible',         1549.99, 15, 'LAP-HP-SPX360-001',     NULL, 1, true),
('Microsoft Surface Laptop 5', 'Intel Core i5-1235U, 8 GB RAM, 256 GB SSD, 13.5" PixelSense touch display',                    999.99, 30, 'LAP-MSFT-SL5-001',      NULL, 1, true),
('Razer Blade 15',             'Intel Core i9-13950HX, 32 GB RAM, 1 TB SSD, 15.6" QHD 240 Hz, NVIDIA RTX 4070',              2499.99, 10, 'LAP-RAZR-BL15-001',     NULL, 1, true),
('Acer Swift 3',               'AMD Ryzen 5 7530U, 8 GB RAM, 512 GB SSD, 14" FHD IPS, lightweight everyday laptop',            649.99, 40, 'LAP-ACER-SW3-001',      NULL, 1, true),
('LG Gram 16',                 'Intel Core i7-1360P, 16 GB RAM, 512 GB SSD, 16" WQXGA IPS, under 1.2 kg',                    1299.99, 14, 'LAP-LG-GRAM16-001',     NULL, 1, true),
('Samsung Galaxy Book3 Ultra', 'Intel Core i9-13900H, 32 GB RAM, 1 TB SSD, 16" Dynamic AMOLED 2X, NVIDIA RTX 4050',          2199.99, 8,  'LAP-SAMS-GB3U-001',     NULL, 1, true),
('Lenovo IdeaPad Flex 5',      'AMD Ryzen 5 7530U, 16 GB RAM, 512 GB SSD, 14" FHD touch 2-in-1, great budget pick',           699.99, 35, 'LAP-LENI-IF5-001',      NULL, 1, true),
('Dell Inspiron 15 3000',      'Intel Core i5-1135G7, 8 GB RAM, 256 GB SSD, 15.6" FHD, reliable everyday laptop',             549.99, 50, 'LAP-DELL-IN15-001',     NULL, 1, true),
('HP Envy x360 15',            'AMD Ryzen 7 7730U, 16 GB RAM, 512 GB SSD, 15.6" FHD touch 2-in-1 OLED',                      1099.99, 20, 'LAP-HP-ENX360-001',     NULL, 1, true),
('ASUS VivoBook 16X',          'Intel Core i7-12700H, 16 GB RAM, 512 GB SSD, 16" FHD IPS, NVIDIA RTX 3050',                   999.99, 28, 'LAP-ASUS-VBK16-001',    NULL, 1, true),
('Acer Predator Helios 300',   'Intel Core i7-12700H, 16 GB RAM, 512 GB SSD, 15.6" FHD 144 Hz, NVIDIA RTX 3060',             1299.99, 17, 'LAP-ACER-PH300-001',    NULL, 1, true),

-- DESKTOPS (10)
('Apple Mac Mini M2 Pro',      'Apple M2 Pro, 16 GB unified memory, 512 GB SSD, compact desktop powerhouse',                  1299.99, 20, 'DSK-APPL-MM2P-001',     NULL, 2, true),
('Dell XPS Desktop 8960',      'Intel Core i9-13900K, 32 GB DDR5, 1 TB SSD + 2 TB HDD, NVIDIA RTX 4070',                    2199.99, 10, 'DSK-DELL-XPS8960-001',  NULL, 2, true),
('ASUS ROG Strix GA35',        'AMD Ryzen 9 7950X, 64 GB DDR5, 2 TB SSD, NVIDIA RTX 4090, full tower gaming beast',          3999.99, 5,  'DSK-ASUS-ROGA35-001',   NULL, 2, true),
('HP Pavilion Desktop TP01',   'Intel Core i5-12400, 16 GB DDR4, 512 GB SSD, NVIDIA GTX 1660 Super, mid-range workhorse',     849.99, 25, 'DSK-HP-PAVTP01-001',    NULL, 2, true),
('Lenovo Legion Tower 5i',     'Intel Core i7-13700F, 32 GB DDR5, 1 TB SSD, NVIDIA RTX 4060 Ti, gaming desktop',            1499.99, 12, 'DSK-LENI-LT5I-001',     NULL, 2, true),
('Intel NUC 13 Pro',           'Intel Core i7-1360P, 16 GB DDR4, 512 GB SSD, ultra-compact mini PC',                          799.99, 18, 'DSK-INTL-NUC13-001',    NULL, 2, true),
('Acer Aspire TC-1780',        'Intel Core i5-13400, 16 GB DDR5, 512 GB SSD + 1 TB HDD, affordable family desktop',           649.99, 30, 'DSK-ACER-TC1780-001',   NULL, 2, true),
('NZXT Player PC H7 Elite',   'Intel Core i7-13700K, 32 GB DDR5, 1 TB SSD, NVIDIA RTX 4070 Ti, premium prebuilt',           2499.99, 7,  'DSK-NZXT-H7E-001',      NULL, 2, true),
('Microsoft Surface Studio 2+','Intel Core i7-11370H, 32 GB RAM, 1 TB SSD, 28" PixelSense touch, all-in-one workstation',    4299.99, 4,  'DSK-MSFT-SS2P-001',     NULL, 2, true),
('Corsair Vengeance i7400',    'Intel Core i9-13900K, 64 GB DDR5, 2 TB SSD, NVIDIA RTX 4080, water-cooled gaming desktop',   3499.99, 6,  'DSK-CORS-VI7400-001',   NULL, 2, true),

-- MONITORS (15)
('LG 27GP950-B UltraGear',     '27" 4K UHD IPS, 160 Hz, 1 ms GtG, HDMI 2.1, DisplayPort 1.4, NVIDIA G-SYNC compatible',     699.99, 30, 'MON-LG-27GP950-001',    NULL, 3, true),
('Samsung Odyssey G9 49"',     '49" Dual QHD VA curved 1000R, 240 Hz, 1 ms, HDR1000, ultra-wide gaming monitor',            1199.99, 12, 'MON-SAMS-G9-001',       NULL, 3, true),
('Dell UltraSharp U2723D',     '27" QHD IPS, USB-C 90 W, built-in KVM switch, factory-calibrated, sRGB 100%',                 599.99, 20, 'MON-DELL-U2723D-001',   NULL, 3, true),
('ASUS ProArt PA279CV',        '27" 4K UHD IPS, USB-C 65 W, 100% sRGB & Rec. 709, hardware calibration, creator monitor',    499.99, 18, 'MON-ASUS-PA279CV-001',  NULL, 3, true),
('BenQ EX2780Q',               '27" QHD IPS, 144 Hz, HDRi, built-in 2.1 speakers, Eye-Care technology, gaming monitor',      449.99, 22, 'MON-BENQ-EX2780-001',   NULL, 3, true),
('Acer Predator X34 GS',       '34" WQHD IPS curved ultra-wide, 180 Hz, 0.5 ms, G-SYNC Ultimate, gaming monitor',           1099.99, 10, 'MON-ACER-X34GS-001',    NULL, 3, true),
('LG 34WP65C-B',               '34" WQHD VA curved ultra-wide, 160 Hz, HDR10, USB-C, FreeSync Premium',                       549.99, 15, 'MON-LG-34WP65-001',     NULL, 3, true),
('HP E24u G4',                 '23.8" FHD IPS, USB-C, 4-port USB hub, height/tilt/swivel/pivot adjustable, business display', 299.99, 35, 'MON-HP-E24UG4-001',     NULL, 3, true),
('MSI MAG274QRF-QD',           '27" QHD IPS Quantum Dot, 165 Hz, 1 ms, FreeSync Premium, wide color gamut gaming monitor',   399.99, 25, 'MON-MSI-274QRF-001',    NULL, 3, true),
('ViewSonic VP2785-4K',        '27" 4K UHD IPS, hardware calibration, 100% Adobe RGB, USB-C 60 W, professional monitor',     799.99, 10, 'MON-VSNI-VP2785-001',   NULL, 3, true),
('Samsung 32" M8 Smart Monitor','32" 4K UHD VA, built-in TV streaming apps, webcam, USB-C 65 W, Bluetooth speaker',           699.99, 14, 'MON-SAMS-M8-32-001',    NULL, 3, true),
('Gigabyte M27Q X',            '27" QHD IPS, 240 Hz, KVM switch, SS IPS panel, HDR400, great-value high-refresh monitor',    399.99, 20, 'MON-GIGA-M27QX-001',    NULL, 3, true),
('ASUS ROG Swift PG32UQX',     '32" 4K UHD IPS, 144 Hz, G-SYNC Ultimate, 1400 nits Mini-LED, top-tier gaming monitor',      2499.99, 5,  'MON-ASUS-PG32UQX-001',  NULL, 3, true),
('Philips 242E2FA',            '23.8" FHD IPS, 100 Hz, AMD FreeSync, EyeComfort, budget-friendly everyday monitor',           179.99, 50, 'MON-PHIL-242E2FA-001',  NULL, 3, true),
('AOC CU34G3S',                '34" WQHD VA curved ultra-wide, 165 Hz, 1 ms MPRT, FreeSync Premium, affordable ultra-wide',  449.99, 18, 'MON-AOC-CU34G3-001',    NULL, 3, true),

-- MICE (10)
('Logitech MX Master 3S',      'Ergonomic wireless mouse, 8000 DPI MagSpeed scroll, Bluetooth & USB, multi-device, silent',    99.99, 60, 'MOU-LOGI-MXM3S-001',    NULL, 4, true),
('Razer DeathAdder V3',        'Ergonomic wired optical gaming mouse, 30 K DPI Focus Pro sensor, 90-hour battery, 59 g',       69.99, 45, 'MOU-RAZR-DAV3-001',     NULL, 4, true),
('SteelSeries Aerox 5',        'Wireless gaming mouse, 18 K DPI TrueMove Air sensor, ultra-lightweight 74 g, IP54 rated',      99.99, 35, 'MOU-STLS-AX5-001',      NULL, 4, true),
('Apple Magic Mouse',          'Multi-Touch surface, rechargeable, wireless, sleek aluminum design, works with Mac & iPad',     79.99, 40, 'MOU-APPL-MM-001',       NULL, 4, true),
('Logitech G Pro X Superlight 2','Ultra-lightweight 60 g wired gaming mouse, HERO 2 25 K DPI sensor, 95-hour battery',        159.99, 25, 'MOU-LOGI-GPXS2-001',    NULL, 4, true),
('Corsair Dark Core RGB Pro',  'Wireless ergonomic gaming mouse, 18 K DPI sensor, Qi wireless charging, 50-hour battery',      79.99, 30, 'MOU-CORS-DCRP-001',     NULL, 4, true),
('Microsoft Arc Mouse',        'Ultra-slim Bluetooth travel mouse, snap-flat design, BlueTrack technology, ergonomic arc',      59.99, 50, 'MOU-MSFT-ARC-001',      NULL, 4, true),
('Glorious Model O Wireless',  '69 g ultra-lightweight honeycomb gaming mouse, 19 K DPI BAMF sensor, 71-hour battery',         79.99, 28, 'MOU-GLOR-MOW-001',      NULL, 4, true),
('Anker Vertical Ergonomic',   '2.4G wireless vertical ergonomic mouse, 5-level DPI, reduces wrist strain, budget-friendly',   35.99, 80, 'MOU-ANKR-VEM-001',      NULL, 4, true),
('ASUS ROG Harpe Ace Aim Lab',  'Wireless gaming mouse, 36 K DPI Aim Lab-tuned sensor, 54 g, 100-hour battery, swappable side buttons', 149.99, 20, 'MOU-ASUS-RHAA-001', NULL, 4, true),

-- KEYBOARDS (8)
('Keychron Q1 Pro QMK',        'Wireless 75% mechanical keyboard, QMK/VIA programmable, hot-swap, gasket-mounted',            199.99, 30, 'KBD-KEYC-Q1P-001',      NULL, 5, true),
('Logitech MX Keys S',         'Full-size wireless keyboard, backlit keys, multi-device, USB-C, 10-day battery life',          109.99, 45, 'KBD-LOGI-MXKS-001',     NULL, 5, true),
('Razer BlackWidow V4 Pro',    'Full-size wireless mechanical keyboard, Razer Yellow switches, Chroma RGB, wrist rest',        229.99, 20, 'KBD-RAZR-BWV4P-001',    NULL, 5, true),
('Apple Magic Keyboard Touch ID','Full-size wireless keyboard with Touch ID, scissor switches, USB-C charging, Mac optimized',  129.99, 40, 'KBD-APPL-MKTID-001',   NULL, 5, true),
('Corsair K100 RGB',           'Full-size wired mechanical keyboard, Cherry MX Speed switches, Axon 44-zone RGB, macro dial', 229.99, 15, 'KBD-CORS-K100-001',     NULL, 5, true),
('SteelSeries Apex Pro TKL',   'TKL wireless mechanical keyboard, adjustable OmniPoint 2.0 switches, OLED display, 40-hour',  199.99, 18, 'KBD-STLS-APTK-001',     NULL, 5, true),
('ASUS ROG Strix Scope II 96', '96% wireless mechanical keyboard, pre-lubed stabilizers, PBT keycaps, ROG NX switches',       149.99, 25, 'KBD-ASUS-SS96-001',     NULL, 5, true),
('Logitech G915 TKL Lightspeed','TKL wireless mechanical keyboard, ultra-thin GL switches, 40-hour battery, LIGHTSPEED 1 ms', 229.99, 22, 'KBD-LOGI-G915TKL-001',  NULL, 5, true),

-- AUDIO (10)
('Sonos Beam Gen 2',           'Compact smart soundbar, Dolby Atmos, HDMI eARC, built-in Alexa & Google Assistant, Wi-Fi',    449.99, 25, 'AUD-SONO-BEAM2-001',    NULL, 6, true),
('Samsung HW-Q990C',           '11.1.4-channel soundbar with rear speakers, Dolby Atmos, DTS:X, 656 W total output',         1499.99, 8,  'AUD-SAMS-Q990C-001',    NULL, 6, true),
('Bose SoundLink Max',         'Premium Bluetooth portable speaker, IP67 waterproof, 20-hour battery, PartyMode',             399.99, 20, 'AUD-BOSE-SLM-001',      NULL, 6, true),
('Sony HT-A7000',              '7.1.2-channel soundbar, Dolby Atmos, 360 Spatial Sound, HDMI eARC, built-in sub & tweeter',  1299.99, 10, 'AUD-SONY-HTA7000-001',  NULL, 6, true),
('Logitech Z407',              '2.1 Bluetooth PC speaker system, 40 W RMS, wireless scroll wheel control, USB-C',              99.99, 40, 'AUD-LOGI-Z407-001',     NULL, 6, true),
('SteelSeries Arctis Nova Pro Wireless','Wireless gaming headset, dual wireless + Bluetooth, active noise cancellation, ClearCast Gen 2 mic', 349.99, 15, 'AUD-STLS-ANPW-001', NULL, 6, true),
('Razer Kraken V3 HyperSense', 'Wired gaming headset, HyperSense haptic technology, THX Spatial Audio, Razer TriForce 50 mm', 129.99, 30, 'AUD-RAZR-KV3H-001',    NULL, 6, true),
('Sony WH-1000XM5',            'Over-ear wireless headset, industry-leading ANC, 30-hour battery, multipoint, LDAC hi-res',   349.99, 35, 'AUD-SONY-XM5-001',      NULL, 6, true),
('Jabra Evolve2 85',           'Professional over-ear wireless headset, ANC, 37-hour battery, certified for Microsoft Teams',  449.99, 12, 'AUD-JABR-EV285-001',    NULL, 6, true),
('Creative Pebble Pro',        '2.0 USB-C/Bluetooth desktop speakers, 16 W RMS, up-firing drivers, minimalist design',         49.99, 60, 'AUD-CREA-PBP-001',      NULL, 6, true),

-- COMPUTER DESKS (8)
('Flexispot E7 Pro Standing Desk','Electric sit-stand desk, dual-motor, 4-memory presets, 355 lb capacity, 60" x 24" top',    599.99, 15, 'DSK-FLEX-E7P-001',      NULL, 7, true),
('UPLIFT V2 Commercial Desk',  'Electric sit-stand desk, 4-leg stability, 535 lb capacity, advanced keypad, 60" x 30" top',   1099.99, 8,  'DSK-UPLT-V2C-001',      NULL, 7, true),
('Secretlab Magnus Pro XL',    'Steel gaming desk with integrated cable management, RGB underglow, magnetic accessories',       849.99, 10, 'DSK-SECL-MGXL-001',     NULL, 7, true),
('IKEA FREDDE Gaming Desk',    'L-shaped gaming desk with monitor shelf, cable management cutout, cupholder, headphone hook',  299.99, 20, 'DSK-IKEA-FRDD-001',     NULL, 7, true),
('Arozzi Arena Gaming Desk',   'Full-size 160 x 82 cm gaming desk, full-surface microfiber mat, cable management, 3 colors',  399.99, 12, 'DSK-AROZ-AREN-001',     NULL, 7, true),
('Autonomous SmartDesk Core',  'Electric height-adjustable desk, dual-motor, 265 lb capacity, 53" x 29" top, budget sit-stand',349.99, 18, 'DSK-AUTS-SDC-001',      NULL, 7, true),
('VIVO 60" Computer Desk',     'Fixed-height 60" wide desk with monitor riser shelf, cable grommets, home office design',      149.99, 35, 'DSK-VIVO-60-001',       NULL, 7, true),
('Eureka Ergonomic Z60 Gaming','Z-shaped gaming desk, carbon fiber texture, RGB lighting strip, monitor shelf, cup holder',    229.99, 22, 'DSK-EURE-Z60-001',      NULL, 7, true),

-- PROJECTORS (8)
('Epson EpiqVision LS500',     '4K Pro-UHD laser projector, 4000 lumens, ultra-short throw, Android TV built-in, 150"',      3999.99, 5,  'PRJ-EPSO-LS500-001',    NULL, 8, true),
('BenQ TH685P',                '1080p home theater projector, 3500 lumens, HDR-PRO, 16 ms input lag, gaming-ready, FPS/RPG mode', 699.99, 12, 'PRJ-BENQ-TH685P-001', NULL, 8, true),
('Optoma UHD35x',              '4K UHD gaming projector, 3600 lumens, 4 ms input lag, 240 Hz supported, HDR10 & HLG',         799.99, 10, 'PRJ-OPTO-UHD35X-001',   NULL, 8, true),
('LG CineBeam HU810PW',        '4K UHD laser projector, 2700 lumens, webOS smart TV, HDR10, 150" screen compatible',         2799.99, 6,  'PRJ-LG-HU810PW-001',    NULL, 8, true),
('Anker Nebula Capsule 3',     'Portable laser pico projector, 1080p, 300 ANSI lumens, Android TV 11, 2.5-hour battery',      749.99, 18, 'PRJ-ANKR-NC3-001',      NULL, 8, true),
('ViewSonic PA503W',           'WXGA business projector, 3800 lumens, HDMI, USB, VGA, 15000-hour lamp life, portable',        449.99, 20, 'PRJ-VSNI-PA503W-001',   NULL, 8, true),
('Samsung The Freestyle',      'Portable 1080p laser projector, 360° rotation, built-in speaker, smart TV OS, 30–100"',       699.99, 14, 'PRJ-SAMS-FRSTL-001',    NULL, 8, true),
('Epson PowerLite 1781W',      'Wireless WXGA ultra-portable projector, 3200 lumens, MHL, USB plug-and-play, 3.8 lbs',        549.99, 10, 'PRJ-EPSO-PL1781-001',   NULL, 8, true),

-- NETWORKING (8)
('ASUS ROG Rapture GT-AXE16000','Quad-band Wi-Fi 6E gaming router, 16000 Mbps, 2.5G & 10G ports, OLED display, MU-MIMO',     549.99, 15, 'NET-ASUS-GT16K-001',    NULL, 9, true),
('TP-Link Archer AXE75',       'Tri-band Wi-Fi 6E router, 6 GHz band, 4804 Mbps, OFDMA, 160 MHz channel, budget Wi-Fi 6E',   199.99, 30, 'NET-TPLI-AXE75-001',    NULL, 9, true),
('Netgear Orbi RBK863S',       'Tri-band Wi-Fi 6 mesh system (3-pack), 6 Gbps, covers 7500 sq ft, 100+ devices',            799.99, 10, 'NET-NTGR-RBK863-001',   NULL, 9, true),
('Ubiquiti UniFi Switch 24',   '24-port managed Gigabit switch, Layer 2, fanless, PoE+ capable, enterprise-grade',            399.99, 12, 'NET-UBIQ-USW24-001',    NULL, 9, true),
('Eero Pro 6E Mesh (3-pack)',  'Tri-band Wi-Fi 6E mesh system, covers 6000 sq ft, works with Alexa, 2.5 GbE WAN port',       599.99, 14, 'NET-EERO-P6E3-001',     NULL, 9, true),
('TP-Link TL-SG108E',          '8-port web-managed Gigabit switch, VLAN, QoS, IGMP, loop prevention, silent fanless design',  39.99, 60, 'NET-TPLI-SG108E-001',   NULL, 9, true),
('GL.iNet GL-MT6000 Flint 2',  'Wi-Fi 6 router, 3548 Mbps, OpenWrt, 2.5G WAN/LAN, VPN accelerator, open-source firmware',   149.99, 25, 'NET-GLIN-MT6000-001',   NULL, 9, true),
('Synology RT6600ax',          'Quad-band Wi-Fi 6 router, 6600 Mbps, SRM OS, VPN, intrusion prevention, 2.5G port',          379.99, 12, 'NET-SYNO-RT6600-001',   NULL, 9, true),

-- STORAGE (16 — to reach 100 total)
('Samsung 990 Pro 2 TB NVMe',  'PCIe 4.0 NVMe SSD, 7450 MB/s read, 6900 MB/s write, TLC NAND, 600 TBW endurance, M.2 2280', 174.99, 50, 'STR-SAMS-990P2T-001',   NULL, 10, true),
('WD Black SN850X 1 TB',       'PCIe 4.0 NVMe SSD, 7300 MB/s read, 6600 MB/s write, Game Mode 2.0, M.2 2280',              129.99, 45, 'STR-WD-SN850X1-001',    NULL, 10, true),
('Seagate Barracuda 4 TB HDD', '3.5" internal SATA HDD, 5400 RPM, 256 MB cache, 6 Gb/s, reliable mass storage',              79.99, 40, 'STR-SGTL-BB4T-001',     NULL, 10, true),
('Crucial MX500 2 TB SATA SSD','2.5" SATA SSD, 560 MB/s read, 510 MB/s write, hardware encryption, excellent value',        134.99, 35, 'STR-CRCL-MX500-001',    NULL, 10, true),
('Samsung T9 4 TB Portable SSD','USB 3.2 Gen 2x2 external SSD, 2000 MB/s read/write, USB-C, IP65 rugged, palm-sized',       299.99, 20, 'STR-SAMS-T94T-001',     NULL, 10, true),
('WD My Passport 5 TB',        'USB 3.0 portable hard drive, password protection, hardware AES-256 encryption, auto backup',   109.99, 30, 'STR-WD-MP5T-001',       NULL, 10, true),
('Lexar NM790 1 TB NVMe',      'PCIe 4.0 NVMe SSD, 7400 MB/s read, 6500 MB/s write, TLC, affordable Gen 4 alternative',      79.99, 40, 'STR-LEXR-NM790-001',    NULL, 10, true),
('Synology DiskStation DS923+','4-bay NAS, AMD Ryzen R1600, 4 GB ECC DDR4, 10GbE upgradable, perfect home/SMB NAS',          599.99, 8,  'STR-SYNO-DS923-001',    NULL, 10, true),
('Kingston Fury Renegade 2 TB','PCIe 4.0 NVMe SSD with heatsink, 7300 MB/s read, 7000 MB/s write, M.2 2280',               159.99, 30, 'STR-KING-FRG2T-001',    NULL, 10, true),
('Sabrent Rocket 4 Plus 4 TB', 'PCIe 4.0 NVMe SSD, 7100 MB/s read, 6600 MB/s write, largest capacity Gen 4 drive',         399.99, 10, 'STR-SABR-R4P4T-001',    NULL, 10, true),
('Seagate IronWolf 8 TB NAS',  '3.5" SATA NAS HDD, 7200 RPM, 256 MB cache, CMR, 180 TB/yr workload, AgileArray tech',       179.99, 18, 'STR-SGTL-IW8T-001',     NULL, 10, true),
('Crucial T500 1 TB NVMe',     'PCIe 5.0 NVMe SSD, 12400 MB/s read, 11800 MB/s write, next-gen blazing speed, M.2 2280',   149.99, 20, 'STR-CRCL-T500-001',     NULL, 10, true),
('G.Skill Trident Z5 RGB 32 GB','DDR5-6000 CL30, 32 GB (2x16), Intel XMP 3.0, RGB heatspreader, desktop RAM kit',           129.99, 25, 'STR-GSKL-TZ5-001',      NULL, 10, true),
('Corsair Vengeance 64 GB DDR5','DDR5-5200 CL38, 64 GB (2x32), Intel XMP 3.0, high-capacity workstation memory kit',        189.99, 15, 'STR-CORS-V64D5-001',    NULL, 10, true),
('SanDisk Extreme Pro 1 TB','USB 3.2 Gen 2 portable SSD, 2000 MB/s read, 2000 MB/s write, IP65, rugged NVMe SSD enclosure',  149.99, 28, 'STR-SAND-EXP1T-001',    NULL, 10, true),
('QNAP TS-464 4-Bay NAS',      '4-bay NAS, Intel Celeron N5095, 8 GB DDR4, 2.5GbE x2, PCIe expansion, 4K transcoding',      599.99, 7,  'STR-QNAP-TS464-001',    NULL, 10, true);
