INSERT IGNORE  INTO season (id, name, description, active, created_by_user_id, modified_by_user_id, created_at, updated_at) VALUES
(1, 'Verano', 'Temporada alta con clima cálido', 1, 1, 1, NOW(), NOW()),
(2, 'Invierno', 'Temporada fría ideal para nieve', 1, 1, 1, NOW(), NOW()),
(3, 'Primavera', 'Clima templado y paisajes florales', 1, 1, 1, NOW(), NOW());

INSERT IGNORE  INTO category (id, name, description, active, created_by_user_id, modified_by_user_id, created_at, updated_at) VALUES
(1, 'Aventura', 'Actividades extremas y excursiones', 1, 1, 1, NOW(), NOW()),
(2, 'Relax', 'Descanso y bienestar', 1, 1, 1, NOW(), NOW()),
(3, 'Cultural', 'Turismo cultural e histórico', 1, 1, 1, NOW(), NOW());

INSERT IGNORE  INTO travel_type (id, name, description, active, created_by_user_id, modified_by_user_id, created_at, updated_at) VALUES
(1, 'Familiar', 'Viajes para toda la familia', 1, 1, 1, NOW(), NOW()),
(2, 'Pareja', 'Viajes románticos', 1, 1, 1, NOW(), NOW()),
(3, 'Solo', 'Viajes individuales', 1, 1, 1, NOW(), NOW());

INSERT IGNORE INTO tour_package (id, name, destination, season_id, category_id, travel_type_id, description, start_date, end_date, price, total_slots, status, stars, image_url, active, created_by_user_id, modified_by_user_id, created_at, updated_at) VALUES
-- 🌴 Cancún
(1, 'Paraíso Todo Incluido en Cancún', 'México', 1, 2, 2,
 'Disfruta de 8 días / 7 noches con vuelos, traslados y alojamiento sin preocupaciones. Todo incluido para una experiencia relajante en el Caribe mexicano.',
 '2026-12-10', '2026-12-17', 850000, 20, 'DISPONIBLE', 5, '/images/cancun.png', 1, 1, 1, NOW(), NOW()),
-- 🏝️ Punta Cana
(2, 'Escapada Premium Todo Incluido en Punta Cana', 'República Dominicana', 1, 2, 2,
 'Vive 6 días / 5 noches de lujo sin límites: vuelos + resort todo incluido con playas de arena blanca y aguas turquesas.',
 '2026-11-05', '2026-11-10', 920000, 15, 'DISPONIBLE', 5, '/images/punta_cana.png', 1, 1, 1, NOW(), NOW()),
-- 🇧🇷 Río de Janeiro
(3, 'Aventura y Sabor en Río de Janeiro', 'Brasil', 3, 1, 3,
 '5 días / 4 noches llenos de energía: vuelos + hotel con desayuno incluido. Ideal para conocer el Cristo Redentor, Pan de Azúcar y las playas más famosas.',
 '2026-10-01', '2026-10-05', 450000, 25, 'DISPONIBLE', 4, '/images/rio.png', 1, 1, 1, NOW(), NOW()),
-- 🇨🇷 Costa Rica
(4, 'Aventura en Costa Rica - Pura Vida', 'Costa Rica', 1, 2, 3,
    'Descubre la biodiversidad de Costa Rica en este increíble viaje de 7 días. Visitarás volcanes, playas paradisíacas, selvas tropicales y tendrás aventuras inolvidables como canopy, rafting y avistamiento de fauna silvestre. Incluye hospedaje en eco-lodges y transporte privado.',
    '2026-07-15', '2026-07-22', 1250000, 25, 'DISPONIBLE', 5, 'https://images5.alphacoders.com/872/872773.jpg', 1, 1, 1, NOW(), NOW()),
-- 🏔️ Patagonia
(5, 'Patagonia Mágica - Torres del Paine', 'Chile / Argentina', 1, 1, 1,
  'Recorrido por los paisajes más impresionantes de la Patagonia. Trekking por el Parque Nacional Torres del Paine, navegación por el Lago Grey y visita al Glaciar Perito Moreno. Ideal para amantes de la naturaleza y la aventura.',
  '2026-11-10', '2026-11-18', 1850000, 15, 'DISPONIBLE', 5, 'https://patagoniaaustral.cl/wp-content/uploads/2025/10/Cuernos_del_Paine_from_Lake_Pehoe-2000x1000.jpg', 1, 1, 1, NOW(), NOW()),
-- 🇨🇴 Caribe Colombiano
(6, 'Caribe Colombiano - Sol y Arena', 'Colombia', 1, 2, 2,
  'Disfruta de las mejores playas del Caribe colombiano. Recorrido por Cartagena, Santa Marta, Tayrona y el Cabo de la Vela. Actividades acuáticas, cultura y gastronomía caribeña.',
  '2026-06-05', '2026-06-12', 980000, 30, 'DISPONIBLE', 4, 'https://larepublica.cronosmedia.glr.pe/original/2021/09/21/614a583261fbd904b705e98b.jpg', 1, 1, 1, NOW(), NOW()),
-- 🇵🇪 Perú
(7, 'Perú Místico - Machu Picchu y más', 'Perú', 1, 1, 1,
  'Descubre la magia del Perú. Recorrido por Lima, Cusco, Valle Sagrado y la maravilla del mundo Machu Picchu. Experiencia cultural, arqueológica y gastronómica única.',
  '2026-08-20', '2026-08-28', 1450000, 20, 'DISPONIBLE', 5, 'https://cdn.mos.cms.futurecdn.net/WFJBpzs4J5x3uvbeKdnm3i.jpg', 1, 1, 1, NOW(), NOW()),
-- 🇯🇵 Japón
(8, 'Japón Tradicional y Moderno', 'Japón', 1, 3, 1,
  'Viaje por lo mejor de Japón: Tokio, Kioto, Osaka y Hiroshima. Experiencia única combinando templos milenarios, tecnología futurista, gastronomía mundial y cultura tradicional japonesa.',
  '2026-09-15', '2026-09-24', 3200000, 12, 'DISPONIBLE', 5, 'https://www.cloud-europamundo.com/img/carousel/hd/Matsumoto_20160401132728.jpg', 1, 1, 1, NOW(), NOW());

INSERT IGNORE  INTO conditions (id, name, description, active, created_at) VALUES
(1, 'Cancelación flexible', 'Cancelación sin costo hasta 48 horas antes', 1, NOW()),
(2, 'No reembolsable', 'No se realizan devoluciones', 1, NOW()),
(3, 'Incluye equipaje', 'Incluye 1 maleta de 23kg', 1, NOW()),
(4, 'No incluye bodega', 'No incluye equipaje en bodega', 1, NOW()),
(5, 'Incluye desayuno', 'Incluye desayuno diario', 1, NOW()),
(6, 'No incluye alimentación', 'No incluye comidas', 1, NOW()),
(7, 'Apto para todo público', 'Sin restricciones de edad', 1, NOW()),
(8, 'Solo mayores de edad', 'Solo mayores de 18 años', 1, NOW()),
(9, 'Itinerario sujeto a cambios', 'Puede cambiar por clima', 1, NOW());

INSERT IGNORE INTO tour_package_condition (tour_package_id, condition_id, active, created_at) VALUES
(1, 1, 1, NOW()),
(1, 3, 1, NOW()),
(1, 5, 1, NOW()),
(1, 7, 1, NOW()),
(1, 9, 1, NOW()),
(2, 1, 1, NOW()),
(2, 3, 1, NOW()),
(2, 5, 1, NOW()),
(2, 7, 1, NOW()),
(2, 9, 1, NOW()),
(3, 2, 1, NOW()),
(3, 4, 1, NOW()),
(3, 5, 1, NOW()),
(3, 7, 1, NOW()),
(3, 9, 1, NOW());

INSERT IGNORE INTO `restriction` (`id`, `name`, `description`, `active`, `created_by_user_id`, `created_at`, `updated_at`) VALUES
(1, 'Edad mínima 18 años', 'Los pasajeros deben tener al menos 18 años.', 1, 1, NOW(), NOW()),
(2, 'Cancelación gratuita 15 días', 'Cancelación sin cargo hasta 15 días antes del viaje.', 1, 1, NOW(), NOW()),
(3, 'No reembolsable', 'Este paquete no permite reembolsos una vez confirmada la reserva.', 1, 1, NOW(), NOW()),
(4, 'Cambios de fecha permitidos', 'Cambios de fecha hasta 10 días antes con recargo del 15%.', 1, 1, NOW(), NOW()),
(5, 'Menores acompañados', 'Los menores de 12 años deben viajar con un adulto responsable.', 1, 1, NOW(), NOW()),
(6, 'Máximo 6 personas por reserva', 'Máximo 6 personas por reserva. Grupos más grandes consultar.', 1, 1, NOW(), NOW()),
(7, 'Pasaporte vigente', 'Pasaporte vigente por al menos 6 meses después de la fecha de regreso.', 1, 1, NOW(), NOW()),
(8, 'Visa requerida', 'Visa según destino. Verificar requisitos antes de reservar.', 1, 1, NOW(), NOW()),
(9, 'Vacuna fiebre amarilla', 'Obligatoria para destinos tropicales y selváticos.', 1, 1, NOW(), NOW()),
(10, 'Seguro de viaje incluido', 'Seguro básico incluido en el paquete. No cubre actividades extremas.', 1, 1, NOW(), NOW()),
(11, 'Restricción de equipaje', '23kg de bodega + 8kg de mano por persona.', 1, 1, NOW(), NOW()),
(12, 'Mascotas no permitidas', 'No se permiten mascotas en este paquete.', 1, 1, NOW(), NOW());

INSERT IGNORE INTO `tour_package_restriction` (`id`, `tour_package_id`, `restriction_id`, `active`, `created_by_user_id`, `modified_by_user_id`, `created_at`, `updated_at`) VALUES
(1, 1, 1, 1, 1, 1, NOW(), NOW()),
(2, 1, 2, 1, 1, 1, NOW(), NOW()),
(3, 1, 5, 1, 1, 1, NOW(), NOW()),
(4, 1, 6, 1, 1, 1, NOW(), NOW()),
(5, 1, 7, 1, 1, 1, NOW(), NOW()),
(6, 1, 10, 1, 1, 1, NOW(), NOW()),
(7, 1, 11, 1, 1, 1, NOW(), NOW()),
(8, 1, 12, 1, 1, 1, NOW(), NOW()),
(9, 2, 1, 1, 1, 1, NOW(), NOW()),
(10, 2, 2, 1, 1, 1, NOW(), NOW()),
(11, 2, 5, 1, 1, 1, NOW(), NOW()),
(12, 2, 6, 1, 1, 1, NOW(), NOW()),
(13, 2, 7, 1, 1, 1, NOW(), NOW()),
(14, 2, 10, 1, 1, 1, NOW(), NOW()),
(15, 2, 11, 1, 1, 1, NOW(), NOW()),
(16, 2, 12, 1, 1, 1, NOW(), NOW()),
(17, 3, 1, 1, 1, 1, NOW(), NOW()),
(18, 3, 3, 1, 1, 1, NOW(), NOW()),
(19, 3, 5, 1, 1, 1, NOW(), NOW()),
(20, 3, 6, 1, 1, 1, NOW(), NOW()),
(21, 3, 7, 1, 1, 1, NOW(), NOW()),
(22, 3, 8, 1, 1, 1, NOW(), NOW()),
(23, 3, 9, 1, 1, 1, NOW(), NOW()),
(24, 3, 10, 1, 1, 1, NOW(), NOW()),
(25, 3, 11, 1, 1, 1, NOW(), NOW()),
(26, 3, 12, 1, 1, 1, NOW(), NOW());

INSERT IGNORE INTO `service` (`id`, `name`, `description`, `active`, `created_by_user_id`, `modified_by_user_id`, `created_at`, `updated_at`) VALUES
(1, 'Vuelo ida y vuelta', 'Incluye tiquetes aéreos ida y vuelta.', 1, 1, 1, NOW(), NOW()),
(2, 'Alojamiento', 'Hospedaje en hoteles seleccionados.', 1, 1, 1, NOW(), NOW()),
(3, 'Todo incluido', 'Comidas y bebidas ilimitadas durante la estancia.', 1, 1, 1, NOW(), NOW()),
(4, 'Desayuno incluido', 'Desayuno diario incluido en el hotel.', 1, 1, 1, NOW(), NOW()),
(5, 'Traslados', 'Traslados aeropuerto - hotel - aeropuerto.', 1, 1, 1, NOW(), NOW()),
(6, 'Seguro de viaje', 'Seguro de asistencia al viajero.', 1, 1, 1, NOW(), NOW()),
(7, 'Tours guiados', 'Recorridos con guía local especializado.', 1, 1, 1, NOW(), NOW()),
(8, 'Entradas a atracciones', 'Ingreso a principales atracciones turísticas.', 1, 1, 1, NOW(), NOW()),
(9, 'Actividades incluidas', 'Actividades recreativas según destino.', 1, 1, 1, NOW(), NOW()),
(10, 'Asistencia 24/7', 'Soporte telefónico durante todo el viaje.', 1, 1, 1, NOW(), NOW());

INSERT IGNORE INTO `tour_package_service` (`id`, `tour_package_id`, `service_id`, `active`, `created_by_user_id`, `modified_by_user_id`, `created_at`, `updated_at`) VALUES
(1, 1, 1, 1, 1, 1, NOW(), NOW()),
(2, 1, 2, 1, 1, 1, NOW(), NOW()),
(3, 1, 3, 1, 1, 1, NOW(), NOW()),
(4, 1, 5, 1, 1, 1, NOW(), NOW()),
(5, 1, 6, 1, 1, 1, NOW(), NOW()),
(6, 1, 9, 1, 1, 1, NOW(), NOW()),
(7, 1, 10, 1, 1, 1, NOW(), NOW()),
(8, 2, 1, 1, 1, 1, NOW(), NOW()),
(9, 2, 2, 1, 1, 1, NOW(), NOW()),
(10, 2, 3, 1, 1, 1, NOW(), NOW()),
(11, 2, 5, 1, 1, 1, NOW(), NOW()),
(12, 2, 6, 1, 1, 1, NOW(), NOW()),
(13, 2, 9, 1, 1, 1, NOW(), NOW()),
(14, 2, 10, 1, 1, 1, NOW(), NOW()),
(15, 3, 1, 1, 1, 1, NOW(), NOW()),
(16, 3, 2, 1, 1, 1, NOW(), NOW()),
(17, 3, 4, 1, 1, 1, NOW(), NOW()),
(18, 3, 5, 1, 1, 1, NOW(), NOW()),
(19, 3, 6, 1, 1, 1, NOW(), NOW()),
(20, 3, 7, 1, 1, 1, NOW(), NOW()),
(21, 3, 8, 1, 1, 1, NOW(), NOW()),
(22, 3, 10, 1, 1, 1, NOW(), NOW());
