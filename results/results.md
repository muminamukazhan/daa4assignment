## LCS and Edit Distance

| n | LCS avg ms | Edit avg ms | LCS length | Edit distance | table cells |
|---|---|---|---|---|---|
| 100 | 0.4308 | 0.4567 | 67 | 56 | 10201 |
| 500 | 1.1842 | 0.9870 | 326 | 263 | 251001 |
| 1000 | 4.3717 | 4.2536 | 648 | 537 | 1002001 |

![dp](dp_time.png)

## String matching: present

| text length | algorithm | avg ms | char comparisons | occurrences |
|---|---|---|---|---|
| 10000 | Naive | 0.3391 | 13416 | 1 |
| 10000 | KMP | 0.3267 | 12529 | 1 |
| 10000 | Rabin-Karp | 0.2358 | 20 | 1 |
| 50000 | Naive | 0.2909 | 66677 | 1 |
| 50000 | KMP | 0.4068 | 61831 | 1 |
| 50000 | Rabin-Karp | 0.9005 | 20 | 1 |
| 100000 | Naive | 0.6779 | 132915 | 1 |
| 100000 | KMP | 0.8988 | 124696 | 1 |
| 100000 | Rabin-Karp | 1.7869 | 20 | 1 |

![present](matching_present.png)

## String matching: absent

| text length | algorithm | avg ms | char comparisons | occurrences |
|---|---|---|---|---|
| 10000 | Naive | 0.0554 | 13426 | 0 |
| 10000 | KMP | 0.0870 | 12565 | 0 |
| 10000 | Rabin-Karp | 0.1765 | 0 | 0 |
| 50000 | Naive | 0.3346 | 66477 | 0 |
| 50000 | KMP | 0.4376 | 62431 | 0 |
| 50000 | Rabin-Karp | 0.8930 | 0 | 0 |
| 100000 | Naive | 0.6731 | 132721 | 0 |
| 100000 | KMP | 0.8797 | 124697 | 0 |
| 100000 | Rabin-Karp | 1.7810 | 0 | 0 |

![absent](matching_absent.png)

## String matching: worst

| text length | algorithm | avg ms | char comparisons | occurrences |
|---|---|---|---|---|
| 10000 | Naive | 0.3757 | 199620 | 0 |
| 10000 | KMP | 0.0834 | 20018 | 0 |
| 10000 | Rabin-Karp | 0.1766 | 0 | 0 |
| 50000 | Naive | 1.8746 | 999620 | 0 |
| 50000 | KMP | 0.4139 | 100018 | 0 |
| 50000 | Rabin-Karp | 0.8931 | 0 | 0 |
| 100000 | Naive | 3.7938 | 1999620 | 0 |
| 100000 | KMP | 0.8322 | 200018 | 0 |
| 100000 | Rabin-Karp | 1.7802 | 0 | 0 |

![worst](matching_worst.png)

## Suffix array (doubling) and Kasai LCP

| n | suffix array avg ms | Kasai avg ms |
|---|---|---|
| 1000 | 0.1475 | 0.1800 |
| 5000 | 0.7842 | 0.3029 |
| 10000 | 1.1278 | 0.2059 |

![kasai](kasai_time.png)
