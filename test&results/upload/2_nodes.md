1.

```
================================================================================
---- Global Information --------------------------------------------------------
> request count                                         20 (OK=20     KO=0     )
> min response time                                  24491 (OK=24491  KO=-     )
> max response time                                  52744 (OK=52744  KO=-     )
> mean response time                                 36883 (OK=36883  KO=-     )
> std deviation                                      10669 (OK=10669  KO=-     )
> response time 50th percentile                      33714 (OK=33714  KO=-     )
> response time 75th percentile                      49177 (OK=49177  KO=-     )
> response time 95th percentile                      52410 (OK=52410  KO=-     )
> response time 99th percentile                      52677 (OK=52677  KO=-     )
> mean requests/sec                                  0.377 (OK=0.377  KO=-     )
---- Response Time Distribution ------------------------------------------------
> t < 800 ms                                             0 (  0%)
> 800 ms < t < 1200 ms                                   0 (  0%)
> t > 1200 ms                                           20 (100%)
> failed                                                 0 (  0%)
================================================================================
```



2.

```
================================================================================
---- Global Information --------------------------------------------------------
> request count                                         20 (OK=20     KO=0     )
> min response time                                  20457 (OK=20457  KO=-     )
> max response time                                  56520 (OK=56520  KO=-     )
> mean response time                                 38637 (OK=38637  KO=-     )
> std deviation                                      13927 (OK=13927  KO=-     )
> response time 50th percentile                      40196 (OK=40196  KO=-     )
> response time 75th percentile                      54349 (OK=54349  KO=-     )
> response time 95th percentile                      56043 (OK=56043  KO=-     )
> response time 99th percentile                      56425 (OK=56425  KO=-     )
> mean requests/sec                                  0.351 (OK=0.351  KO=-     )
---- Response Time Distribution ------------------------------------------------
> t < 800 ms                                             0 (  0%)
> 800 ms < t < 1200 ms                                   0 (  0%)
> t > 1200 ms                                           20 (100%)
> failed                                                 0 (  0%)
================================================================================
```



3.

```
================================================================================
---- Global Information --------------------------------------------------------
> request count                                         20 (OK=18     KO=2     )
> min response time                                  21181 (OK=21181  KO=60009 )
> max response time                                  60009 (OK=41753  KO=60009 )
> mean response time                                 37557 (OK=35062  KO=60009 )
> std deviation                                      10326 (OK=7499   KO=0     )
> response time 50th percentile                      39156 (OK=38790  KO=60009 )
> response time 75th percentile                      40753 (OK=40278  KO=60009 )
> response time 95th percentile                      60009 (OK=41442  KO=60009 )
> response time 99th percentile                      60009 (OK=41691  KO=60009 )
> mean requests/sec                                  0.328 (OK=0.295  KO=0.033 )
---- Response Time Distribution ------------------------------------------------
> t < 800 ms                                             0 (  0%)
> 800 ms < t < 1200 ms                                   0 (  0%)
> t > 1200 ms                                           18 ( 90%)
> failed                                                 2 ( 10%)
---- Errors --------------------------------------------------------------------
> i.g.h.c.i.RequestTimeoutException: Request timeout to localhos      2 (100.0%)
t/127.0.0.1:8080 after 60000 ms
================================================================================
```

