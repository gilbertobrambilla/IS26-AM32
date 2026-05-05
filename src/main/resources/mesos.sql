--
-- Database: `mesos`
--

-- --------------------------------------------------------

--
-- Table structure for table `building_cards`
--

CREATE TABLE `building_cards` (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `era` int DEFAULT NULL,
  `base_prestige` int DEFAULT NULL,
  `cost` int DEFAULT NULL,
  `effect_code` varchar(50) DEFAULT NULL,
  `effect_params` json DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `building_cards`
--

INSERT INTO `building_cards` (`id`, `era`, `base_prestige`, `cost`, `effect_code`, `effect_params`) VALUES
('building_01', 1, 3, 5, 'SUSTENANCE_DISCOUNT_PER_CHARACTER', '{\"targetCharacter\": \"ARTIST\", \"discountPerCharacter\": 1}'),
('building_02', 1, 4, 4, 'SUSTENANCE_DISCOUNT_PER_CHARACTER', '{\"targetCharacter\": \"GATHERER\", \"discountPerCharacter\": 1}'),
('building_03', 2, 4, 7, 'SUSTENANCE_DISCOUNT_PER_CHARACTER', '{\"targetCharacter\": \"INVENTOR\", \"discountPerCharacter\": 1}'),
('building_04', 1, 2, 5, 'SHAMANIC_PENALTY_IMMUNITY', '{}'),
('building_05', 1, 3, 3, 'EXTRA_FOOD_ON_TOTEM', '{\"extraFood\": 1}'),
('building_06', 1, 4, 3, 'FOOD_PER_INVENTOR_PAIR', '{\"extraFood\": 3}'),
('building_07', 2, 0, 7, 'SHAMANIC_DOUBLE_REWARD', '{}'),
('building_08', 2, 4, 6, 'EXTRA_SHAMAN_STARS', '{\"extraStars\": 3}'),
('building_09', 2, 6, 5, 'EXTRA_RESOURCES_ON_EVENT', '{\"extraFood\": 1, \"targetEvent\": \"CAVE_PAINTINGS\", \"extraPrestige\": 0, \"targetCharacter\": \"ARTIST\"}'),
('building_10', 2, 2, 7, 'EXTRA_RESOURCES_ON_EVENT', '{\"extraFood\": 1, \"targetEvent\": \"HUNT\", \"extraPrestige\": 1, \"targetCharacter\": \"HUNTER\"}'),
('building_11', 2, 4, 6, 'ENDGAME_BUILDER_PRESTIGE_MULTIPLIER', '{\"multiplier\": 2}'),
('building_12', 1, 3, 4, 'FOOD_PER_SET', '{\"extraFood\": 5}'),
('building_13', 2, 6, 5, 'ENDGAME_PRESTIGE_PER_SET', '{\"pointsPerSet\": 6}'),
('building_14', 3, 8, 8, 'ENDGAME_PRESTIGE_PER_CHARACTER', '{\"targetCharacter\": \"HUNTER\", \"pointsPerCharacter\": 3}'),
('building_15', 3, 6, 6, 'ENDGAME_PRESTIGE_PER_CHARACTER', '{\"targetCharacter\": \"INVENTOR\", \"pointsPerCharacter\": 2}'),
('building_16', 3, 4, 7, 'ENDGAME_PRESTIGE_PER_CHARACTER', '{\"targetCharacter\": \"ARTIST\", \"pointsPerCharacter\": 4}'),
('building_17', 3, 4, 7, 'ENDGAME_PRESTIGE_PER_CHARACTER', '{\"targetCharacter\": \"SHAMAN\", \"pointsPerCharacter\": 4}'),
('building_18', 3, 6, 7, 'ENDGAME_PRESTIGE_PER_CHARACTER', '{\"targetCharacter\": \"GATHERER\", \"pointsPerCharacter\": 4}'),
('building_19', 3, 3, 6, 'ENDGAME_PRESTIGE_PER_CHARACTER', '{\"targetCharacter\": \"BUILDER\", \"pointsPerCharacter\": 4}'),
('building_20', 3, 3, 9, 'EXTRA_CARD_ON_ROUND_END', '{}'),
('building_21', 3, 25, 10, 'NO_EFFECT', '{}');

-- --------------------------------------------------------

--
-- Table structure for table `character_cards`
--

CREATE TABLE `character_cards` (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `era` int DEFAULT NULL,
  `base_prestige` int DEFAULT NULL,
  `min_players` int DEFAULT NULL,
  `character_type` varchar(20) DEFAULT NULL,
  `character_params` json DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `character_cards`
--

INSERT INTO `character_cards` (`id`, `era`, `base_prestige`, `min_players`, `character_type`, `character_params`) VALUES
('character_01', 3, 0, 0, 'ARTIST', '{\"art_prestige_per_pair\": 10}'),
('character_02', 3, 0, 0, 'ARTIST', '{\"art_prestige_per_pair\": 10}'),
('character_03', 3, 0, 0, 'ARTIST', '{\"art_prestige_per_pair\": 10}'),
('character_04', 2, 0, 0, 'ARTIST', '{\"art_prestige_per_pair\": 10}'),
('character_05', 2, 0, 0, 'ARTIST', '{\"art_prestige_per_pair\": 10}'),
('character_06', 2, 0, 0, 'ARTIST', '{\"art_prestige_per_pair\": 10}'),
('character_07', 1, 0, 0, 'ARTIST', '{\"art_prestige_per_pair\": 10}'),
('character_08', 1, 0, 0, 'ARTIST', '{\"art_prestige_per_pair\": 10}'),
('character_09', 1, 0, 0, 'ARTIST', '{\"art_prestige_per_pair\": 10}'),
('character_10', 2, 0, 3, 'ARTIST', '{\"art_prestige_per_pair\": 10}'),
('character_11', 1, 0, 3, 'ARTIST', '{\"art_prestige_per_pair\": 10}'),
('character_12', 1, 0, 4, 'ARTIST', '{\"art_prestige_per_pair\": 10}'),
('character_13', 3, 0, 5, 'ARTIST', '{\"art_prestige_per_pair\": 10}'),
('character_14', 1, 2, 0, 'BUILDER', '{\"bld_building_discount\": 1}'),
('character_15', 1, 3, 0, 'BUILDER', '{\"bld_building_discount\": 1}'),
('character_16', 1, 0, 0, 'BUILDER', '{\"bld_building_discount\": 2}'),
('character_17', 1, 1, 5, 'BUILDER', '{\"bld_building_discount\": 2}'),
('character_18', 2, 2, 3, 'BUILDER', '{\"bld_building_discount\": 1}'),
('character_19', 2, 1, 0, 'BUILDER', '{\"bld_building_discount\": 2}'),
('character_20', 2, 4, 0, 'BUILDER', '{\"bld_building_discount\": 1}'),
('character_21', 2, 3, 0, 'BUILDER', '{\"bld_building_discount\": 2}'),
('character_22', 3, 3, 0, 'BUILDER', '{\"bld_building_discount\": 2}'),
('character_23', 3, 2, 0, 'BUILDER', '{\"bld_building_discount\": 2}'),
('character_24', 3, 5, 0, 'BUILDER', '{\"bld_building_discount\": 1}'),
('character_25', 3, 4, 5, 'BUILDER', '{\"bld_building_discount\": 1}'),
('character_26', 1, 0, 3, 'GATHERER', '{\"gat_sustenance_discount\": 3}'),
('character_27', 1, 0, 5, 'GATHERER', '{\"gat_sustenance_discount\": 3}'),
('character_28', 1, 0, 0, 'GATHERER', '{\"gat_sustenance_discount\": 3}'),
('character_29', 1, 0, 0, 'GATHERER', '{\"gat_sustenance_discount\": 3}'),
('character_30', 2, 0, 5, 'GATHERER', '{\"gat_sustenance_discount\": 3}'),
('character_31', 2, 0, 4, 'GATHERER', '{\"gat_sustenance_discount\": 3}'),
('character_32', 2, 0, 3, 'GATHERER', '{\"gat_sustenance_discount\": 3}'),
('character_33', 2, 0, 0, 'GATHERER', '{\"gat_sustenance_discount\": 3}'),
('character_34', 3, 0, 5, 'GATHERER', '{\"gat_sustenance_discount\": 3}'),
('character_35', 3, 0, 4, 'GATHERER', '{\"gat_sustenance_discount\": 3}'),
('character_36', 3, 0, 0, 'GATHERER', '{\"gat_sustenance_discount\": 3}'),
('character_37', 1, 0, 3, 'HUNTER', '{\"hnt_has_meat\": 0}'),
('character_38', 1, 0, 3, 'HUNTER', '{\"hnt_has_meat\": 0}'),
('character_39', 1, 0, 0, 'HUNTER', '{\"hnt_has_meat\": 1}'),
('character_40', 1, 0, 0, 'HUNTER', '{\"hnt_has_meat\": 1}'),
('character_41', 1, 0, 0, 'HUNTER', '{\"hnt_has_meat\": 0}'),
('character_42', 2, 0, 5, 'HUNTER', '{\"hnt_has_meat\": 0}'),
('character_43', 2, 0, 4, 'HUNTER', '{\"hnt_has_meat\": 1}'),
('character_44', 2, 0, 3, 'HUNTER', '{\"hnt_has_meat\": 1}'),
('character_45', 2, 0, 0, 'HUNTER', '{\"hnt_has_meat\": 1}'),
('character_46', 2, 0, 0, 'HUNTER', '{\"hnt_has_meat\": 0}'),
('character_47', 2, 0, 0, 'HUNTER', '{\"hnt_has_meat\": 0}'),
('character_48', 3, 0, 5, 'HUNTER', '{\"hnt_has_meat\": 1}'),
('character_49', 3, 0, 0, 'HUNTER', '{\"hnt_has_meat\": 1}'),
('character_50', 3, 0, 0, 'HUNTER', '{\"hnt_has_meat\": 0}'),
('character_51', 3, 0, 0, 'HUNTER', '{\"hnt_has_meat\": 0}'),
('character_52', 1, 0, 4, 'INVENTOR', '{\"inv_invention_type\": \"ROPE\"}'),
('character_53', 1, 0, 4, 'INVENTOR', '{\"inv_invention_type\": \"FLUTE\"}'),
('character_54', 1, 0, 4, 'INVENTOR', '{\"inv_invention_type\": \"POTTERY\"}'),
('character_55', 1, 0, 0, 'INVENTOR', '{\"inv_invention_type\": \"CANOE\"}'),
('character_56', 1, 0, 0, 'INVENTOR', '{\"inv_invention_type\": \"ARROWHEAD\"}'),
('character_57', 1, 0, 0, 'INVENTOR', '{\"inv_invention_type\": \"LEATHER\"}'),
('character_58', 1, 0, 0, 'INVENTOR', '{\"inv_invention_type\": \"BREAD\"}'),
('character_59', 2, 0, 4, 'INVENTOR', '{\"inv_invention_type\": \"FISHHOOK\"}'),
('character_60', 2, 0, 0, 'INVENTOR', '{\"inv_invention_type\": \"IDOL\"}'),
('character_61', 2, 0, 0, 'INVENTOR', '{\"inv_invention_type\": \"LEATHER\"}'),
('character_62', 2, 0, 0, 'INVENTOR', '{\"inv_invention_type\": \"POTTERY\"}'),
('character_63', 2, 0, 0, 'INVENTOR', '{\"inv_invention_type\": \"FLUTE\"}'),
('character_64', 2, 0, 0, 'INVENTOR', '{\"inv_invention_type\": \"ROPE\"}'),
('character_65', 3, 0, 4, 'INVENTOR', '{\"inv_invention_type\": \"NECKLACE\"}'),
('character_66', 3, 0, 3, 'INVENTOR', '{\"inv_invention_type\": \"CANOE\"}'),
('character_67', 3, 0, 3, 'INVENTOR', '{\"inv_invention_type\": \"ARROWHEAD\"}'),
('character_68', 3, 0, 0, 'INVENTOR', '{\"inv_invention_type\": \"BREAD\"}'),
('character_69', 3, 0, 0, 'INVENTOR', '{\"inv_invention_type\": \"NECKLACE\"}'),
('character_70', 3, 0, 0, 'INVENTOR', '{\"inv_invention_type\": \"FISHHOOK\"}'),
('character_71', 3, 0, 0, 'INVENTOR', '{\"inv_invention_type\": \"IDOL\"}'),
('character_72', 1, 0, 5, 'SHAMAN', '{\"sha_shaman_stars\": 2}'),
('character_73', 1, 0, 4, 'SHAMAN', '{\"sha_shaman_stars\": 1}'),
('character_74', 1, 0, 0, 'SHAMAN', '{\"sha_shaman_stars\": 2}'),
('character_75', 1, 0, 0, 'SHAMAN', '{\"sha_shaman_stars\": 1}'),
('character_76', 2, 0, 5, 'SHAMAN', '{\"sha_shaman_stars\": 2}'),
('character_77', 2, 0, 5, 'SHAMAN', '{\"sha_shaman_stars\": 1}'),
('character_78', 2, 0, 0, 'SHAMAN', '{\"sha_shaman_stars\": 2}'),
('character_79', 2, 0, 0, 'SHAMAN', '{\"sha_shaman_stars\": 2}'),
('character_80', 3, 0, 4, 'SHAMAN', '{\"sha_shaman_stars\": 2}'),
('character_81', 3, 0, 3, 'SHAMAN', '{\"sha_shaman_stars\": 2}'),
('character_82', 3, 0, 0, 'SHAMAN', '{\"sha_shaman_stars\": 3}'),
('character_83', 3, 0, 0, 'SHAMAN', '{\"sha_shaman_stars\": 3}'),
('character_84', 3, 0, 0, 'SHAMAN', '{\"sha_shaman_stars\": 2}');

-- --------------------------------------------------------

--
-- Table structure for table `event_cards`
--

CREATE TABLE `event_cards` (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `era` int DEFAULT NULL,
  `event_type` varchar(20) DEFAULT NULL,
  `event_params` json DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `event_cards`
--

INSERT INTO `event_cards` (`id`, `era`, `event_type`, `event_params`) VALUES
('event_01', 1, 'CAVE_PAINTINGS', '{\"cave_prestige_bonus\": 1, \"cave_bonus_threshold\": 1, \"cave_prestige_penalty\": 2, \"cave_penalty_threshold\": 0}'),
('event_02', 2, 'CAVE_PAINTINGS', '{\"cave_prestige_bonus\": 2, \"cave_bonus_threshold\": 2, \"cave_prestige_penalty\": 2, \"cave_penalty_threshold\": 1}'),
('event_03', 3, 'CAVE_PAINTINGS', '{\"cave_prestige_bonus\": 3, \"cave_bonus_threshold\": 3, \"cave_prestige_penalty\": 2, \"cave_penalty_threshold\": 2}'),
('event_04', 1, 'HUNT', '{\"hunt_food_bonus\": 1, \"hunt_prestige_bonus\": 1}'),
('event_05', 2, 'HUNT', '{\"hunt_food_bonus\": 1, \"hunt_prestige_bonus\": 2}'),
('event_06', 3, 'HUNT', '{\"hunt_food_bonus\": 1, \"hunt_prestige_bonus\": 3}'),
('event_07', 1, 'SHAMANIC_RITUAL', '{\"sham_prestige_bonus\": 5, \"sham_prestige_penalty\": 3}'),
('event_08', 2, 'SHAMANIC_RITUAL', '{\"sham_prestige_bonus\": 10, \"sham_prestige_penalty\": 5}'),
('event_09', 3, 'SHAMANIC_RITUAL', '{\"is_final\": true, \"sham_prestige_bonus\": 15, \"sham_prestige_penalty\": 7}'),
('event_10', 1, 'SUSTENANCE', '{\"sust_food_cost\": 1, \"sust_prestige_penalty\": 1}'),
('event_11', 2, 'SUSTENANCE', '{\"sust_food_cost\": 1, \"sust_prestige_penalty\": 2}'),
('event_12', 3, 'SUSTENANCE', '{\"is_final\": true, \"sust_food_cost\": 1, \"sust_prestige_penalty\": 3}');

-- --------------------------------------------------------

--
-- Table structure for table `offer_tiles`
--

CREATE TABLE `offer_tiles` (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `min_players` int DEFAULT NULL,
  `act_draws_upper` int DEFAULT NULL,
  `act_draws_lower` int DEFAULT NULL,
  `act_food_bonus` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `offer_tiles`
--

INSERT INTO `offer_tiles` (`id`, `min_players`, `act_draws_upper`, `act_draws_lower`, `act_food_bonus`) VALUES
('offer_tile_A', 5, 0, 0, 3),
('offer_tile_B', 0, 0, 1, 0),
('offer_tile_C', 0, 1, 0, 0),
('offer_tile_D', 3, 0, 2, 0),
('offer_tile_E', 0, 1, 1, 0),
('offer_tile_F', 0, 2, 0, 0),
('offer_tile_G', 4, 2, 1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `turn_order_tiles`
--

CREATE TABLE `turn_order_tiles` (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `required_players` int DEFAULT NULL,
  `food_modifiers` json DEFAULT NULL,
  `prestige_modifiers` json DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `turn_order_tiles`
--

INSERT INTO `turn_order_tiles` (`id`, `required_players`, `food_modifiers`, `prestige_modifiers`) VALUES
('order_tile_2', 2, '[1, -1]', '[0, -2]'),
('order_tile_3', 3, '[2, 0, -1]', '[0, 0, -2]'),
('order_tile_4', 4, '[2, 1, 0, -1]', '[0, 0, 0, -2]'),
('order_tile_5', 5, '[3, 1, 0, 0, -1]', '[0, 0, 0, 0, -2]');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `building_cards`
--
ALTER TABLE `building_cards`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `character_cards`
--
ALTER TABLE `character_cards`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `event_cards`
--
ALTER TABLE `event_cards`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `offer_tiles`
--
ALTER TABLE `offer_tiles`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `turn_order_tiles`
--
ALTER TABLE `turn_order_tiles`
  ADD PRIMARY KEY (`id`);
COMMIT;
