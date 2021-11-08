# Run complete. Total time: 02:50:41

REMEMBER: The numbers below are just data. To gain reusable insights, you need to follow up on
why the numbers are the way they are. Use profilers (see -prof, -lprof), design factorial
experiments, perform baseline and negative tests that provide experimental control, make sure
the benchmarking environment is safe on JVM/OS/HW level, ask for reviews from the domain experts.
Do not assume the numbers tell you what you want them to tell.

Benchmark                                                                       Mode      Cnt        Score        Error   Units
PrimeNumbers.parallelPrimesTo_1000                                             thrpt       25        0.022 ±      0.001  ops/us
PrimeNumbers.parallelPrimesTo_10_000                                           thrpt       25        0.003 ±      0.001  ops/us
PrimeNumbers.parallelPrimesTo_10_000_000                                       thrpt       25       ≈ 10??
ops/us
PrimeNumbers.serialPrimesTo_1000                                               thrpt       25        0.029 ±      0.001  ops/us
PrimeNumbers.serialPrimesTo_10_000                                             thrpt       25        0.002 ±      0.001  ops/us
PrimeNumbers.serialPrimesTo_10_000_000                                         thrpt       25       ≈ 10??
ops/us
PrimeNumbers.parallelPrimesTo_1000                                              avgt       25       46.620 ±      1.549   us/op
PrimeNumbers.parallelPrimesTo_10_000                                            avgt       25      379.036 ±      7.604   us/op
PrimeNumbers.parallelPrimesTo_10_000_000                                        avgt       25  1360362.823 ±  14723.084   us/op
PrimeNumbers.serialPrimesTo_1000                                                avgt       25       32.998 ±      0.315   us/op
PrimeNumbers.serialPrimesTo_10_000                                              avgt       25      644.467 ±     16.160   us/op
PrimeNumbers.serialPrimesTo_10_000_000                                          avgt       25  8199848.272 ±  84514.067   us/op
PrimeNumbers.parallelPrimesTo_1000                                            sample  2777856       44.950 ±      0.044   us/op
PrimeNumbers.parallelPrimesTo_1000:parallelPrimesTo_1000·p0.00                sample                34.944
us/op
PrimeNumbers.parallelPrimesTo_1000:parallelPrimesTo_1000·p0.50                sample                38.976
us/op
PrimeNumbers.parallelPrimesTo_1000:parallelPrimesTo_1000·p0.90                sample                60.096
us/op
PrimeNumbers.parallelPrimesTo_1000:parallelPrimesTo_1000·p0.95                sample                74.368
us/op
PrimeNumbers.parallelPrimesTo_1000:parallelPrimesTo_1000·p0.99                sample                86.912
us/op
PrimeNumbers.parallelPrimesTo_1000:parallelPrimesTo_1000·p0.999               sample               145.152
us/op
PrimeNumbers.parallelPrimesTo_1000:parallelPrimesTo_1000·p0.9999              sample               832.512
us/op
PrimeNumbers.parallelPrimesTo_1000:parallelPrimesTo_1000·p1.00                sample             16400.384
us/op
PrimeNumbers.parallelPrimesTo_10_000                                          sample   692165      360.984 ±      0.315   us/op
PrimeNumbers.parallelPrimesTo_10_000:parallelPrimesTo_10_000·p0.00            sample               299.520
us/op
PrimeNumbers.parallelPrimesTo_10_000:parallelPrimesTo_10_000·p0.50            sample               325.120
us/op
PrimeNumbers.parallelPrimesTo_10_000:parallelPrimesTo_10_000·p0.90            sample               470.016
us/op
PrimeNumbers.parallelPrimesTo_10_000:parallelPrimesTo_10_000·p0.95            sample               486.912
us/op
PrimeNumbers.parallelPrimesTo_10_000:parallelPrimesTo_10_000·p0.99            sample               523.776
us/op
PrimeNumbers.parallelPrimesTo_10_000:parallelPrimesTo_10_000·p0.999           sample               639.830
us/op
PrimeNumbers.parallelPrimesTo_10_000:parallelPrimesTo_10_000·p0.9999          sample              1502.345
us/op
PrimeNumbers.parallelPrimesTo_10_000:parallelPrimesTo_10_000·p1.00            sample             23068.672
us/op
PrimeNumbers.parallelPrimesTo_10_000_000                                      sample      197  1379489.553 ±  17122.156   us/op
PrimeNumbers.parallelPrimesTo_10_000_000:parallelPrimesTo_10_000_000·p0.00    sample           1270874.112
us/op
PrimeNumbers.parallelPrimesTo_10_000_000:parallelPrimesTo_10_000_000·p0.50    sample           1373634.560
us/op
PrimeNumbers.parallelPrimesTo_10_000_000:parallelPrimesTo_10_000_000·p0.90    sample           1447034.880
us/op
PrimeNumbers.parallelPrimesTo_10_000_000:parallelPrimesTo_10_000_000·p0.95    sample           1504077.414
us/op
PrimeNumbers.parallelPrimesTo_10_000_000:parallelPrimesTo_10_000_000·p0.99    sample           1699196.436
us/op
PrimeNumbers.parallelPrimesTo_10_000_000:parallelPrimesTo_10_000_000·p0.999   sample           1826619.392
us/op
PrimeNumbers.parallelPrimesTo_10_000_000:parallelPrimesTo_10_000_000·p0.9999  sample           1826619.392
us/op
PrimeNumbers.parallelPrimesTo_10_000_000:parallelPrimesTo_10_000_000·p1.00    sample           1826619.392
us/op
PrimeNumbers.serialPrimesTo_1000                                              sample  3574997       34.954 ±      0.017   us/op
PrimeNumbers.serialPrimesTo_1000:serialPrimesTo_1000·p0.00                    sample                29.088
us/op
PrimeNumbers.serialPrimesTo_1000:serialPrimesTo_1000·p0.50                    sample                33.088
us/op
PrimeNumbers.serialPrimesTo_1000:serialPrimesTo_1000·p0.90                    sample                43.648
us/op
PrimeNumbers.serialPrimesTo_1000:serialPrimesTo_1000·p0.95                    sample                45.888
us/op
PrimeNumbers.serialPrimesTo_1000:serialPrimesTo_1000·p0.99                    sample                56.064
us/op
PrimeNumbers.serialPrimesTo_1000:serialPrimesTo_1000·p0.999                   sample               112.256
us/op
PrimeNumbers.serialPrimesTo_1000:serialPrimesTo_1000·p0.9999                  sample               245.888
us/op
PrimeNumbers.serialPrimesTo_1000:serialPrimesTo_1000·p1.00                    sample              2252.800
us/op
PrimeNumbers.serialPrimesTo_10_000                                            sample   391385      638.627 ±      0.527   us/op
PrimeNumbers.serialPrimesTo_10_000:serialPrimesTo_10_000·p0.00                sample               556.032
us/op
PrimeNumbers.serialPrimesTo_10_000:serialPrimesTo_10_000·p0.50                sample               584.704
us/op
PrimeNumbers.serialPrimesTo_10_000:serialPrimesTo_10_000·p0.90                sample               828.416
us/op
PrimeNumbers.serialPrimesTo_10_000:serialPrimesTo_10_000·p0.95                sample               850.944
us/op
PrimeNumbers.serialPrimesTo_10_000:serialPrimesTo_10_000·p0.99                sample               926.720
us/op
PrimeNumbers.serialPrimesTo_10_000:serialPrimesTo_10_000·p0.999               sample              1099.776
us/op
PrimeNumbers.serialPrimesTo_10_000:serialPrimesTo_10_000·p0.9999              sample              1533.100
us/op
PrimeNumbers.serialPrimesTo_10_000:serialPrimesTo_10_000·p1.00                sample              3104.768
us/op
PrimeNumbers.serialPrimesTo_10_000_000                                        sample       50  8189965.763 ±  77491.849   us/op
PrimeNumbers.serialPrimesTo_10_000_000:serialPrimesTo_10_000_000·p0.00        sample           8044675.072
us/op
PrimeNumbers.serialPrimesTo_10_000_000:serialPrimesTo_10_000_000·p0.50        sample           8128561.152
us/op
PrimeNumbers.serialPrimesTo_10_000_000:serialPrimesTo_10_000_000·p0.90        sample           8469977.498
us/op
PrimeNumbers.serialPrimesTo_10_000_000:serialPrimesTo_10_000_000·p0.95        sample           8598323.200
us/op
PrimeNumbers.serialPrimesTo_10_000_000:serialPrimesTo_10_000_000·p0.99        sample           8724152.320
us/op
PrimeNumbers.serialPrimesTo_10_000_000:serialPrimesTo_10_000_000·p0.999       sample           8724152.320
us/op
PrimeNumbers.serialPrimesTo_10_000_000:serialPrimesTo_10_000_000·p0.9999      sample           8724152.320
us/op
PrimeNumbers.serialPrimesTo_10_000_000:serialPrimesTo_10_000_000·p1.00        sample           8724152.320
us/op
PrimeNumbers.parallelPrimesTo_1000                                                ss        5    17313.480 ±   4656.925   us/op
$
PrimeNumbers.parallelPrimesTo_10_000_000                                          ss        5  1393561.400 ± 192815.029   us/op
PrimeNumbers.serialPrimesTo_1000                                                  ss        5    11659.600 ±   3063.276   us/op
PrimeNumbers.serialPrimesTo_10_000                                                ss        5    16312.860 ±   7333.104   us/op
PrimeNumbers.serialPrimesTo_10_000_000                                            ss        5  8178701.020 ± 119051.726   us/op


------

REMEMBER: The numbers below are just data. To gain reusable insights, you need to follow up on
why the numbers are the way they are. Use profilers (see -prof, -lprof), design factorial
experiments, perform baseline and negative tests that provide experimental control, make sure
the benchmarking environment is safe on JVM/OS/HW level, ask for reviews from the domain experts.
Do not assume the numbers tell you what you want them to tell.

Benchmark                                 Mode  Cnt         Score         Error  Units
PrimeThreads.virtualPrimesTo_1000         avgt   25     32569.341 ±     189.899  us/op
PrimeThreads.virtualPrimesTo_10_000       avgt   25     57198.866 ±     362.116  us/op
PrimeThreads.virtualPrimesTo_10_000_000   avgt   25  35332423.480 ± 1221438.387  us/op