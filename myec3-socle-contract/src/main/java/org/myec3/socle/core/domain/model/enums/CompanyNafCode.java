/**
 * Copyright (c) 2011 Atos Bourgogne
 * 
 * This file is part of MyEc3.
 * 
 * MyEc3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as published by
 * the Free Software Foundation.
 * 
 * MyEc3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with MyEc3. If not, see <http://www.gnu.org/licenses/>.
 */
package org.myec3.socle.core.domain.model.enums;

import org.myec3.socle.core.domain.model.Company;

/**
 * 
 * Enum that contains a list of available NAF Codes for a {@link Company}.
 * 
 * @author Anthony Colas <anthonyjcolas@atosorigin.com>
 * 
 */
public enum CompanyNafCode implements StructureNafCode {

	_01_11Z("0111Z"), _01_12Z("0112Z"), _01_13Z("0113Z"), _01_14Z("0114Z"), _01_15Z(
			"0115Z"), _01_16Z("0116Z"), _01_19Z("0119Z"), _01_21Z("0121Z"), _01_22Z(
			"0122Z"), _01_23Z("0123Z"), _01_24Z("0124Z"), _01_25Z("0125Z"), _01_26Z(
			"0126Z"), _01_27Z("0127Z"), _01_28Z("0128Z"), _01_29Z("0129Z"), _01_30Z(
			"0130Z"), _01_41Z("0141Z"), _01_42Z("0142Z"), _01_43Z("0143Z"), _01_44Z(
			"0144Z"), _01_45Z("0145Z"), _01_46Z("0146Z"), _01_47Z("0147Z"), _01_49Z(
			"0149Z"), _01_50Z("0150Z"), _01_61Z("0161Z"), _01_62Z("0162Z"), _01_63Z(
			"0163Z"), _01_64Z("0164Z"), _01_70Z("0170Z"), _02_10Z("0210Z"), _02_20Z(
			"0220Z"), _02_30Z("0230Z"), _02_40Z("0240Z"), _03_11Z("0311Z"), _03_12Z(
			"0312Z"), _03_21Z("0321Z"), _03_22Z("0322Z"), _05_10Z("0510Z"), _05_20Z(
			"0520Z"), _06_10Z("0610Z"), _06_20Z("0620Z"), _07_10Z("0710Z"), _07_21Z(
			"0721Z"), _07_29Z("0729Z"), _08_11Z("0811Z"), _08_12Z("0812Z"), _08_91Z(
			"0891Z"), _08_92Z("0892Z"), _08_93Z("0893Z"), _08_99Z("0899Z"), _09_10Z(
			"0910Z"), _09_90Z("0990Z"), _10_11Z("1011Z"), _10_12Z("1012Z"), _10_13A(
			"1013A"), _10_13B("1013B"), _10_20Z("1020Z"), _10_31Z("1031Z"), _10_32Z(
			"1032Z"), _10_39A("1039A"), _10_39B("1039B"), _10_41A("1041A"), _10_41B(
			"1041B"), _10_42Z("1042Z"), _10_51A("1051A"), _10_51B("1051B"), _10_51C(
			"1051C"), _10_51D("1051D"), _10_52Z("1052Z"), _10_61A("1061A"), _10_61B(
			"1061B"), _10_62Z("1062Z"), _10_71A("1071A"), _10_71B("1071B"), _10_71C(
			"1071C"), _10_71D("1071D"), _10_72Z("1072Z"), _10_73Z("1073Z"), _10_81Z(
			"1081Z"), _10_82Z("1082Z"), _10_83Z("1083Z"), _10_84Z("1084Z"), _10_85Z(
			"1085Z"), _10_86Z("1086Z"), _10_89Z("1089Z"), _10_91Z("1091Z"), _10_92Z(
			"1092Z"), _11_01Z("1101Z"), _11_02A("1102A"), _11_02B("1102B"), _11_03Z(
			"1103Z"), _11_04Z("1104Z"), _11_05Z("1105Z"), _11_06Z("1106Z"), _11_07A(
			"1107A"), _11_07B("1107B"), _12_00Z("1200Z"), _13_10Z("1310Z"), _13_20Z(
			"1320Z"), _13_30Z("1330Z"), _13_91Z("1391Z"), _13_92Z("1392Z"), _13_93Z(
			"1393Z"), _13_94Z("1394Z"), _13_95Z("1395Z"), _13_96Z("1396Z"), _13_99Z(
			"1399Z"), _14_11Z("1411Z"), _14_12Z("1412Z"), _14_13Z("1413Z"), _14_14Z(
			"1414Z"), _14_19Z("1419Z"), _14_20Z("1420Z"), _14_31Z("1431Z"), _14_39Z(
			"1439Z"), _15_11Z("1511Z"), _15_12Z("1512Z"), _16_10A("1610A"), _16_10B(
			"1610B"), _16_21Z("1621Z"), _16_22Z("1622Z"), _16_23Z("1623Z"), _16_24Z(
			"1624Z"), _16_29Z("1629Z"), _17_11Z("1711Z"), _17_12Z("1712Z"), _17_21A(
			"1721A"), _17_21B("1721B"), _17_21C("1721C"), _17_22Z("1722Z"), _17_23Z(
			"1723Z"), _17_24Z("1724Z"), _17_29Z("1729Z"), _18_11Z("1811Z"), _18_12Z(
			"1812Z"), _18_13Z("1813Z"), _18_14Z("1814Z"), _18_20Z("1820Z"), _19_10Z(
			"1910Z"), _19_20Z("1920Z"), _20_11Z("2011Z"), _20_12Z("2012Z"), _20_13A(
			"2013A"), _20_13B("2013B"), _20_14Z("2014Z"), _20_15Z("2015Z"), _20_16Z(
			"2016Z"), _20_17Z("2017Z"), _20_20Z("2020Z"), _20_30Z("2030Z"), _20_41Z(
			"2041Z"), _20_42Z("2042Z"), _20_51Z("2051Z"), _20_52Z("2052Z"), _20_53Z(
			"2053Z"), _20_59Z("2059Z"), _20_60Z("2060Z"), _21_10Z("2110Z"), _21_20Z(
			"2120Z"), _22_11Z("2211Z"), _22_19Z("2219Z"), _22_21Z("2221Z"), _22_22Z(
			"2222Z"), _22_23Z("2223Z"), _22_29A("2229A"), _22_29B("2229B"), _23_11Z(
			"2311Z"), _23_12Z("2312Z"), _23_13Z("2313Z"), _23_14Z("2314Z"), _23_19Z(
			"2319Z"), _23_20Z("2320Z"), _23_31Z("2331Z"), _23_32Z("2332Z"), _23_41Z(
			"2341Z"), _23_42Z("2342Z"), _23_43Z("2343Z"), _23_44Z("2344Z"), _23_49Z(
			"2349Z"), _23_51Z("2351Z"), _23_52Z("2352Z"), _23_61Z("2361Z"), _23_62Z(
			"2362Z"), _23_63Z("2363Z"), _23_64Z("2364Z"), _23_65Z("2365Z"), _23_69Z(
			"2369Z"), _23_70Z("2370Z"), _23_91Z("2391Z"), _23_99Z("2399Z"), _24_10Z(
			"2410Z"), _24_20Z("2420Z"), _24_31Z("2431Z"), _24_32Z("2432Z"), _24_33Z(
			"2433Z"), _24_34Z("2434Z"), _24_41Z("2441Z"), _24_42Z("2442Z"), _24_43Z(
			"2443Z"), _24_44Z("2444Z"), _24_45Z("2445Z"), _24_46Z("2446Z"), _24_51Z(
			"2451Z"), _24_52Z("2452Z"), _24_53Z("2453Z"), _24_54Z("2454Z"), _25_11Z(
			"2511Z"), _25_12Z("2512Z"), _25_21Z("2521Z"), _25_29Z("2529Z"), _25_30Z(
			"2530Z"), _25_40Z("2540Z"), _25_50A("2550A"), _25_50B("2550B"), _25_61Z(
			"2561Z"), _25_62A("2562A"), _25_62B("2562B"), _25_71Z("2571Z"), _25_72Z(
			"2572Z"), _25_73A("2573A"), _25_73B("2573B"), _25_91Z("2591Z"), _25_92Z(
			"2592Z"), _25_93Z("2593Z"), _25_94Z("2594Z"), _25_99A("2599A"), _25_99B(
			"2599B"), _26_11Z("2611Z"), _26_12Z("2612Z"), _26_20Z("2620Z"), _26_30Z(
			"2630Z"), _26_40Z("2640Z"), _26_51A("2651A"), _26_51B("2651B"), _26_52Z(
			"2652Z"), _26_60Z("2660Z"), _26_70Z("2670Z"), _26_80Z("2680Z"), _27_11Z(
			"2711Z"), _27_12Z("2712Z"), _27_20Z("2720Z"), _27_31Z("2731Z"), _27_32Z(
			"2732Z"), _27_33Z("2733Z"), _27_40Z("2740Z"), _27_51Z("2751Z"), _27_52Z(
			"2752Z"), _27_90Z("2790Z"), _28_11Z("2811Z"), _28_12Z("2812Z"), _28_13Z(
			"2813Z"), _28_14Z("2814Z"), _28_15Z("2815Z"), _28_21Z("2821Z"), _28_22Z(
			"2822Z"), _28_23Z("2823Z"), _28_24Z("2824Z"), _28_25Z("2825Z"), _28_29A(
			"2829A"), _28_29B("2829B"), _28_30Z("2830Z"), _28_41Z("2841Z"), _28_49Z(
			"2849Z"), _28_91Z("2891Z"), _28_92Z("2892Z"), _28_93Z("2893Z"), _28_94Z(
			"2894Z"), _28_95Z("2895Z"), _28_96Z("2896Z"), _28_99A("2899A"), _28_99B(
			"2899B"), _29_10Z("2910Z"), _29_20Z("2920Z"), _29_31Z("2931Z"), _29_32Z(
			"2932Z"), _30_11Z("3011Z"), _30_12Z("3012Z"), _30_20Z("3020Z"), _30_30Z(
			"3030Z"), _30_40Z("3040Z"), _30_91Z("3091Z"), _30_92Z("3092Z"), _30_99Z(
			"3099Z"), _31_01Z("3101Z"), _31_02Z("3102Z"), _31_03Z("3103Z"), _31_09A(
			"3109A"), _31_09B("3109B"), _32_11Z("3211Z"), _32_12Z("3212Z"), _32_13Z(
			"3213Z"), _32_20Z("3220Z"), _32_30Z("3230Z"), _32_40Z("3240Z"), _32_50A(
			"3250A"), _32_50B("3250B"), _32_91Z("3291Z"), _32_99Z("3299Z"), _33_11Z(
			"3311Z"), _33_12Z("3312Z"), _33_13Z("3313Z"), _33_14Z("3314Z"), _33_15Z(
			"3315Z"), _33_16Z("3316Z"), _33_17Z("3317Z"), _33_19Z("3319Z"), _33_20A(
			"3320A"), _33_20B("3320B"), _33_20C("3320C"), _33_20D("3320D"), _35_11Z(
			"3511Z"), _35_12Z("3512Z"), _35_13Z("3513Z"), _35_14Z("3514Z"), _35_21Z(
			"3521Z"), _35_22Z("3522Z"), _35_23Z("3523Z"), _35_30Z("3530Z"), _36_00Z(
			"3600Z"), _37_00Z("3700Z"), _38_11Z("3811Z"), _38_12Z("3812Z"), _38_21Z(
			"3821Z"), _38_22Z("3822Z"), _38_31Z("3831Z"), _38_32Z("3832Z"), _39_00Z(
			"3900Z"), _41_10A("4110A"), _41_10B("4110B"), _41_10C("4110C"), _41_10D(
			"4110D"), _41_20A("4120A"), _41_20B("4120B"), _42_11Z("4211Z"), _42_12Z(
			"4212Z"), _42_13A("4213A"), _42_13B("4213B"), _42_21Z("4221Z"), _42_22Z(
			"4222Z"), _42_91Z("4291Z"), _42_99Z("4299Z"), _43_11Z("4311Z"), _43_12A(
			"4312A"), _43_12B("4312B"), _43_13Z("4313Z"), _43_21A("4321A"), _43_21B(
			"4321B"), _43_22A("4322A"), _43_22B("4322B"), _43_29A("4329A"), _43_29B(
			"4329B"), _43_31Z("4331Z"), _43_32A("4332A"), _43_32B("4332B"), _43_32C(
			"4332C"), _43_33Z("4333Z"), _43_34Z("4334Z"), _43_39Z("4339Z"), _43_91A(
			"4391A"), _43_91B("4391B"), _43_99A("4399A"), _43_99B("4399B"), _43_99C(
			"4399C"), _43_99D("4399D"), _43_99E("4399E"), _45_11Z("4511Z"), _45_19Z(
			"4519Z"), _45_20A("4520A"), _45_20B("4520B"), _45_31Z("4531Z"), _45_32Z(
			"4532Z"), _45_40Z("4540Z"), _46_11Z("4611Z"), _46_12A("4612A"), _46_12B(
			"4612B"), _46_13Z("4613Z"), _46_14Z("4614Z"), _46_15Z("4615Z"), _46_16Z(
			"4616Z"), _46_17A("4617A"), _46_17B("4617B"), _46_18Z("4618Z"), _46_19A(
			"4619A"), _46_19B("4619B"), _46_21Z("4621Z"), _46_22Z("4622Z"), _46_23Z(
			"4623Z"), _46_24Z("4624Z"), _46_31Z("4631Z"), _46_32A("4632A"), _46_32B(
			"4632B"), _46_32C("4632C"), _46_33Z("4633Z"), _46_34Z("4634Z"), _46_35Z(
			"4635Z"), _46_36Z("4636Z"), _46_37Z("4637Z"), _46_38A("4638A"), _46_38B(
			"4638B"), _46_39A("4639A"), _46_39B("4639B"), _46_41Z("4641Z"), _46_42Z(
			"4642Z"), _46_43Z("4643Z"), _46_44Z("4644Z"), _46_45Z("4645Z"), _46_46Z(
			"4646Z"), _46_47Z("4647Z"), _46_48Z("4648Z"), _46_49Z("4649Z"), _46_51Z(
			"4651Z"), _46_52Z("4652Z"), _46_61Z("4661Z"), _46_62Z("4662Z"), _46_63Z(
			"4663Z"), _46_64Z("4664Z"), _46_65Z("4665Z"), _46_66Z("4666Z"), _46_69A(
			"4669A"), _46_69B("4669B"), _46_69C("4669C"), _46_71Z("4671Z"), _46_72Z(
			"4672Z"), _46_73A("4673A"), _46_73B("4673B"), _46_74A("4674A"), _46_74B(
			"4674B"), _46_75Z("4675Z"), _46_76Z("4676Z"), _46_77Z("4677Z"), _46_90Z(
			"4690Z"), _47_11A("4711A"), _47_11B("4711B"), _47_11C("4711C"), _47_11D(
			"4711D"), _47_11E("4711E"), _47_11F("4711F"), _47_19A("4719A"), _47_19B(
			"4719B"), _47_21Z("4721Z"), _47_22Z("4722Z"), _47_23Z("4723Z"), _47_24Z(
			"4724Z"), _47_25Z("4725Z"), _47_26Z("4726Z"), _47_29Z("4729Z"), _47_30Z(
			"4730Z"), _47_41Z("4741Z"), _47_42Z("4742Z"), _47_43Z("4743Z"), _47_51Z(
			"4751Z"), _47_52A("4752A"), _47_52B("4752B"), _47_53Z("4753Z"), _47_54Z(
			"4754Z"), _47_59A("4759A"), _47_59B("4759B"), _47_61Z("4761Z"), _47_62Z(
			"4762Z"), _47_63Z("4763Z"), _47_64Z("4764Z"), _47_65Z("4765Z"), _47_71Z(
			"4771Z"), _47_72A("4772A"), _47_72B("4772B"), _47_73Z("4773Z"), _47_74Z(
			"4774Z"), _47_75Z("4775Z"), _47_76Z("4776Z"), _47_77Z("4777Z"), _47_78A(
			"4778A"), _47_78B("4778B"), _47_78C("4778C"), _47_79Z("4779Z"), _47_81Z(
			"4781Z"), _47_82Z("4782Z"), _47_89Z("4789Z"), _47_91A("4791A"), _47_91B(
			"4791B"), _47_99A("4799A"), _47_99B("4799B"), _49_10Z("4910Z"), _49_20Z(
			"4920Z"), _49_31Z("4931Z"), _49_32Z("4932Z"), _49_39A("4939A"), _49_39B(
			"4939B"), _49_39C("4939C"), _49_41A("4941A"), _49_41B("4941B"), _49_41C(
			"4941C"), _49_42Z("4942Z"), _49_50Z("4950Z"), _50_10Z("5010Z"), _50_20Z(
			"5020Z"), _50_30Z("5030Z"), _50_40Z("5040Z"), _51_10Z("5110Z"), _51_21Z(
			"5121Z"), _51_22Z("5122Z"), _52_10A("5210A"), _52_10B("5210B"), _52_21Z(
			"5221Z"), _52_22Z("5222Z"), _52_23Z("5223Z"), _52_24A("5224A"), _52_24B(
			"5224B"), _52_29A("5229A"), _52_29B("5229B"), _53_10Z("5310Z"), _53_20Z(
			"5320Z"), _55_10Z("5510Z"), _55_20Z("5520Z"), _55_30Z("5530Z"), _55_90Z(
			"5590Z"), _56_10A("5610A"), _56_10B("5610B"), _56_10C("5610C"), _56_21Z(
			"5621Z"), _56_29A("5629A"), _56_29B("5629B"), _56_30Z("5630Z"), _58_11Z(
			"5811Z"), _58_12Z("5812Z"), _58_13Z("5813Z"), _58_14Z("5814Z"), _58_19Z(
			"5819Z"), _58_21Z("5821Z"), _58_29A("5829A"), _58_29B("5829B"), _58_29C(
			"5829C"), _59_11A("5911A"), _59_11B("5911B"), _59_11C("5911C"), _59_12Z(
			"5912Z"), _59_13A("5913A"), _59_13B("5913B"), _59_14Z("5914Z"), _59_20Z(
			"5920Z"), _60_10Z("6010Z"), _60_20A("6020A"), _60_20B("6020B"), _61_10Z(
			"6110Z"), _61_20Z("6120Z"), _61_30Z("6130Z"), _61_90Z("6190Z"), _62_01Z(
			"6201Z"), _62_02A("6202A"), _62_02B("6202B"), _62_03Z("6203Z"), _62_09Z(
			"6209Z"), _63_11Z("6311Z"), _63_12Z("6312Z"), _63_91Z("6391Z"), _63_99Z(
			"6399Z"), _64_11Z("6411Z"), _64_19Z("6419Z"), _64_20Z("6420Z"), _64_30Z(
			"6430Z"), _64_91Z("6491Z"), _64_92Z("6492Z"), _64_99Z("6499Z"), _65_11Z(
			"6511Z"), _65_12Z("6512Z"), _65_20Z("6520Z"), _65_30Z("6530Z"), _66_11Z(
			"6611Z"), _66_12Z("6612Z"), _66_19A("6619A"), _66_19B("6619B"), _66_21Z(
			"6621Z"), _66_22Z("6622Z"), _66_29Z("6629Z"), _66_30Z("6630Z"), _68_10Z(
			"6810Z"), _68_20A("6820A"), _68_20B("6820B"), _68_31Z("6831Z"), _68_32A(
			"6832A"), _68_32B("6832B"), _69_10Z("6910Z"), _69_20Z("6920Z"), _70_10Z(
			"7010Z"), _70_21Z("7021Z"), _70_22Z("7022Z"), _71_11Z("7111Z"), _71_12A(
			"7112A"), _71_12B("7112B"), _71_20A("7120A"), _71_20B("7120B"), _72_11Z(
			"7211Z"), _72_19Z("7219Z"), _72_20Z("7220Z"), _73_11Z("7311Z"), _73_12Z(
			"7312Z"), _73_20Z("7320Z"), _74_10Z("7410Z"), _74_20Z("7420Z"), _74_30Z(
			"7430Z"), _74_90A("7490A"), _74_90B("7490B"), _75_00Z("7500Z"), _77_11A(
			"7711A"), _77_11B("7711B"), _77_12Z("7712Z"), _77_21Z("7721Z"), _77_22Z(
			"7722Z"), _77_29Z("7729Z"), _77_31Z("7731Z"), _77_32Z("7732Z"), _77_33Z(
			"7733Z"), _77_34Z("7734Z"), _77_35Z("7735Z"), _77_39Z("7739Z"), _77_40Z(
			"7740Z"), _78_10Z("7810Z"), _78_20Z("7820Z"), _78_30Z("7830Z"), _79_11Z(
			"7911Z"), _79_12Z("7912Z"), _79_90Z("7990Z"), _80_10Z("8010Z"), _80_20Z(
			"8020Z"), _80_30Z("8030Z"), _81_10Z("8110Z"), _81_21Z("8121Z"), _81_22Z(
			"8122Z"), _81_29A("8129A"), _81_29B("8129B"), _81_30Z("8130Z"), _82_11Z(
			"8211Z"), _82_19Z("8219Z"), _82_20Z("8220Z"), _82_30Z("8230Z"), _82_91Z(
			"8291Z"), _82_92Z("8292Z"), _82_99Z("8299Z"), _84_11Z("8411Z"), _84_12Z(
			"8412Z"), _84_13Z("8413Z"), _84_21Z("8421Z"), _84_22Z("8422Z"), _84_23Z(
			"8423Z"), _84_24Z("8424Z"), _84_25Z("8425Z"), _84_30A("8430A"), _84_30B(
			"8430B"), _84_30C("8430C"), _85_10Z("8510Z"), _85_20Z("8520Z"), _85_31Z(
			"8531Z"), _85_32Z("8532Z"), _85_41Z("8541Z"), _85_42Z("8542Z"), _85_51Z(
			"8551Z"), _85_52Z("8552Z"), _85_53Z("8553Z"), _85_59A("8559A"), _85_59B(
			"8559B"), _85_60Z("8560Z"), _86_10Z("8610Z"), _86_21Z("8621Z"), _86_22A(
			"8622A"), _86_22B("8622B"), _86_22C("8622C"), _86_23Z("8623Z"), _86_90A(
			"8690A"), _86_90B("8690B"), _86_90C("8690C"), _86_90D("8690D"), _86_90E(
			"8690E"), _86_90F("8690F"), _87_10A("8710A"), _87_10B("8710B"), _87_10C(
			"8710C"), _87_20A("8720A"), _87_20B("8720B"), _87_30A("8730A"), _87_30B(
			"8730B"), _87_90A("8790A"), _87_90B("8790B"), _88_10A("8810A"), _88_10B(
			"8810B"), _88_10C("8810C"), _88_91A("8891A"), _88_91B("8891B"), _88_99A(
			"8899A"), _88_99B("8899B"), _90_01Z("9001Z"), _90_02Z("9002Z"), _90_03A(
			"9003A"), _90_03B("9003B"), _90_04Z("9004Z"), _91_01Z("9101Z"), _91_02Z(
			"9102Z"), _91_03Z("9103Z"), _91_04Z("9104Z"), _92_00Z("9200Z"), _93_11Z(
			"9311Z"), _93_12Z("9312Z"), _93_13Z("9313Z"), _93_19Z("9319Z"), _93_21Z(
			"9321Z"), _93_29Z("9329Z"), _94_11Z("9411Z"), _94_12Z("9412Z"), _94_20Z(
			"9420Z"), _94_91Z("9491Z"), _94_92Z("9492Z"), _94_99Z("9499Z"), _95_11Z(
			"9511Z"), _95_12Z("9512Z"), _95_21Z("9521Z"), _95_22Z("9522Z"), _95_23Z(
			"9523Z"), _95_24Z("9524Z"), _95_25Z("9525Z"), _95_29Z("9529Z"), _96_01A(
			"9601A"), _96_01B("9601B"), _96_02A("9602A"), _96_02B("9602B"), _96_03Z(
			"9603Z"), _96_04Z("9604Z"), _96_09Z("9609Z"), _97_00Z("9700Z"), _98_10Z(
			"9810Z"), _98_20Z("9820Z"), _99_00Z("9900Z");

	private final String apeCode;

	/**
	 * Constructor. Initialize ape code.
	 * 
	 * @param apeCode
	 *            : the ape code to set
	 */
	private CompanyNafCode(String apeCode) {
		this.apeCode = apeCode;
	}

	/**
	 * @return a string value containing the ape code value
	 */
	public String getApeCode() {
		return apeCode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.apeCode;
	}
}
