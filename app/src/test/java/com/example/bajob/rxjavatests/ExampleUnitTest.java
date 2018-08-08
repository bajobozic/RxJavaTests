package com.example.bajob.rxjavatests;

import com.jakewharton.rx.ReplayingShare;

import org.junit.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.internal.schedulers.IoScheduler;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    String hi = "Hello";
    String name = "Savo";
    String greattins = " Greattings";
    final List<String> stringList = Arrays.asList(hi, name, greattins);
    final List<String> integerList = Arrays.asList("5", "4", "10");
    final List<Integer> intList = Arrays.asList(5, 4, 11, 2, 8);

    private static void log(Object msg) {
        System.out.println(Thread.currentThread().getName() + ": " + msg);
    }

    private <T> void disposeObservable(DisposableObserver<T> disposableObserver) {
        if (disposableObserver != null && !disposableObserver.isDisposed()) {
            disposableObserver.dispose();
            log("Unsubscribed");

        }
    }

    private int someLongOperationThatReturnInteger() {
        try {
            log("long runnig operation started");
            Thread.sleep(3000);
            log("long runnig operation finished");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 123;
    }

    private List<Integer> someLongWebServiceOperationThatReturnIntegerList() {
        try {
            log("long runnig list operation started");
            log("Loading .");
            Thread.sleep(250);
            log("Loading ..");
            Thread.sleep(250);
            log("Loading ...");
            Thread.sleep(250);
            log("Loading ....");
            Thread.sleep(250);
            log("Loading .....");
            Thread.sleep(250);
            log("Loading ......");
            Thread.sleep(250);
            log("long runnig list operation finished");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(1, 5, 2, 34, 3, 9, 87);
    }

    private Observable<Integer> someLongOperationThatReturnObservable() {
        try {
            log("long runnig operation that return observable started");
            Thread.sleep(3000);
            log("long runnig operation that return observable finished");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Observable.just(123456);
    }

    private Observable<Integer> someLongOperationThatReturnObservable(final Integer id) {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Observable.just(123456 * id).doOnSubscribe(disposable -> log("long runnig operation that return observable started"))
                .doOnComplete(() -> log("long runnig operation that return observable finished"));
    }

    private Observable<Integer> someLongOperationThatReturnObservableConcurrent(final Integer id) {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Observable.defer(() -> Observable.just(12345 * id))
                .doOnSubscribe(disposable -> log("long runnig operation that return observable started"))
                .doOnComplete(() -> log("long runnig operation that return observable finished"))
                .subscribeOn(Schedulers.computation());
    }

    private Observable<Integer> someLongOperationThatReturnObservableConcurrentDelay(final Integer id) {
        return Observable.defer(() -> Observable.timer(10 * id, TimeUnit.MILLISECONDS).map(aLong -> 12345 * id))
                .doOnSubscribe(disposable -> log("long runnig operation that return observable started"))
                .doOnComplete(() -> log("long runnig operation that return observable finished"))
                .subscribeOn(Schedulers.computation());
    }

    @Test
    public void justTest() {
        final DisposableObserver<String> disposableObserver = Observable.
                just("some string").
                subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        log("onNext " + s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        log(" " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        log("onCompleated");
                    }
                });
        disposeObservable(disposableObserver);
    }


    @Test
    public void justLongBackgroundOperationOperationRight() {
        final Observable<Integer> just = Observable.just(someLongOperationThatReturnInteger());
        final DisposableObserver<Integer> disposableObserver = just
//                .subscribeOn(new ComputationScheduler())
                .subscribeOn(new IoScheduler())
                .subscribeWith(new DisposableObserver<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        log("onNext " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        log("" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        log("onCompleated");
                    }
                });
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposeObservable(disposableObserver);
        }

    }

    @Test
    public void rxTimer() {
        //read postDelay() aka timer(...)
        final Observable<Long> timerObservable = Observable.timer(3, TimeUnit.SECONDS);
        final DisposableObserver<Long> disposableObserver = timerObservable.subscribeWith(new DisposableObserver<Long>() {
            @Override
            public void onNext(Long aLong) {
                log("onNext " + aLong);
            }

            @Override
            public void onError(Throwable e) {
                log("" + e.getMessage());
            }

            @Override
            public void onComplete() {
                log("onCompleated");
            }
        });
        disposeObservable(disposableObserver);
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } finally {
//            disposeObservable(disposableObserver);
//        }

    }

    @Test
    public void rxTimerRightWay() {
        final Observable<Long> timerObservable = Observable.timer(3, TimeUnit.SECONDS);
        final DisposableObserver<Long> disposableObserver = timerObservable.subscribeWith(new DisposableObserver<Long>() {
            @Override
            public void onNext(Long aLong) {
                log("onNext " + aLong);
            }

            @Override
            public void onError(Throwable e) {
                log("" + e.getMessage());
            }

            @Override
            public void onComplete() {
                log("onCompleated");
            }
        });
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposeObservable(disposableObserver);
        }

    }

    @Test
    public void rxTimerSwitchThreads() {
        final Observable<Long> timerObservable = Observable.timer(3, TimeUnit.SECONDS);
        final DisposableObserver<Long> disposableObserver = timerObservable
                .doOnNext(l -> log(l))
                .observeOn(Schedulers.newThread())
                .subscribeWith(new DisposableObserver<Long>() {
                    @Override
                    public void onNext(Long aLong) {
                        log("onNext " + aLong);
                    }

                    @Override
                    public void onError(Throwable e) {
                        log("" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        log("onCompleated");
                    }
                });
//     disposeObservable(disposableObserver);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposeObservable(disposableObserver);
        }

    }

    @Test
    public void rxInterval() {
        //great for periodic pooling
        final DisposableObserver<Long> disposableObserver = Observable
                .interval(1000, 500, TimeUnit.MILLISECONDS)
                .subscribeWith(new DisposableObserver<Long>() {
                    @Override
                    public void onNext(Long aLong) {
                        log("onNext " + aLong);
                    }

                    @Override
                    public void onError(Throwable e) {
                        log("" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        log("onCompleated");
                    }
                });

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposeObservable(disposableObserver);
        }
    }

    @Test
    public void rxJustLongOperation() {
        final Observable<Integer> longRunningObservable = Observable.just(someLongOperationThatReturnInteger());
        final DisposableObserver<Integer> disposableObserver = longRunningObservable
                .subscribeWith(new DisposableObserver<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        log("onNext " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        log(" " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        log("onCompleated");
                    }
                });

        disposeObservable(disposableObserver);
    }

    @Test
    public void rxJustLongBackgroundOperation() {
        final Observable<Integer> longRunningObservable = Observable.just(someLongOperationThatReturnInteger());
//        try {
//            log("after long running operation");
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        final DisposableObserver<Integer> disposableObserver = longRunningObservable
//                .subscribeOn(new ComputationScheduler())
                .subscribeOn(new IoScheduler())
                .subscribeWith(new DisposableObserver<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        log("onNext " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        log("" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        log("onCompleated");
                    }
                });

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposeObservable(disposableObserver);
        }
    }

    @Test
    public void rxJustLongBackgroundOperationThatReturnsObservable() {
        final Observable<Integer> longRunningObservable = Observable.defer(() -> someLongOperationThatReturnObservable());
        final DisposableObserver<Integer> disposableObserver = longRunningObservable
//                .subscribeOn(new ComputationScheduler())
                .subscribeOn(new IoScheduler())
                .subscribeWith(new DisposableObserver<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        log("onNext " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        log("" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        log("onCompleated");
                    }
                });

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposeObservable(disposableObserver);
        }
    }


    @Test
    public void rxJavaMerge() {
        final DisposableObserver<String> disposableObserver = Observable
                .merge(Observable.interval(50, TimeUnit.MILLISECONDS)
                                .take(stringList.size())
                                .map(aLong -> stringList.get(aLong.intValue()))
                        ,
                        Observable.interval(40, TimeUnit.MILLISECONDS)
                                .take(integerList.size())
                                .map(aLong -> integerList.get(aLong.intValue())))
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        log("onNext " + s);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposeObservable(disposableObserver);
        }
    }

    @Test
    public void rxJavaConcat() {
        final DisposableObserver<String> disposableObserver = Observable
                .concat(Observable.interval(50, TimeUnit.MILLISECONDS)
                                .take(stringList.size())
                                .map(aLong -> stringList.get(aLong.intValue()))
                        ,
                        Observable.interval(40, TimeUnit.MILLISECONDS)
                                .take(integerList.size())
                                .map(aLong -> integerList.get(aLong.intValue())))
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        log("onNext " + s);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposeObservable(disposableObserver);
        }
    }

    @Test
    public void rxJavaMergeSecond() {
        final DisposableObserver<String> disposableObserver = Observable
                .merge(Observable.fromIterable(stringList)
                        , Observable.fromIterable(integerList))
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        log("onNext " + s);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposeObservable(disposableObserver);
        }
    }

    @Test
    public void rxJavaFlatMap() {
        final DisposableObserver<String> disposableObserver = Observable
                .just("Lorem", "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing", "elit")
                .delay(word -> Observable.timer(word.length(), TimeUnit.SECONDS))
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        log("onNext " + s);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        try {
            Thread.sleep(12000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposeObservable(disposableObserver);
        }
    }

    @Test
    public void rxJavaFlatMapSecond() {
        final DisposableObserver<Serializable> disposableObserver = Observable.interval(500, 1000, TimeUnit.MILLISECONDS)
                .take(4)
                .flatMap(l -> Observable.just(l),
                        throwable -> Observable.error(throwable),
                        () -> Observable.fromIterable(integerList))
                .subscribeWith(new DisposableObserver<Serializable>() {
                    @Override
                    public void onNext(Serializable serializable) {
                        log("onNext " + serializable.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        log("onError " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });


        try {
            Thread.sleep(12000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposeObservable(disposableObserver);
        }
    }

    @Test
    public void rxJavaFlatMapWithError() {
        final DisposableObserver<Serializable> disposableObserver = Observable.interval(500, 1000, TimeUnit.MILLISECONDS)
                .flatMap(aLong -> aLong == 2 ? Observable.error(new RuntimeException("Some exception")) : Observable.just(aLong))
                .take(4)
                .flatMap(l -> Observable.just(l),
                        throwable -> Observable.error(throwable),
                        () -> Observable.fromIterable(integerList))
                .subscribeWith(new DisposableObserver<Serializable>() {
                    @Override
                    public void onNext(Serializable serializable) {
                        log("onNext " + serializable.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        log("onError " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });


        try {
            Thread.sleep(12000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposeObservable(disposableObserver);
        }
    }

    @Test
    public void rxJavaMergeTakeFirst() {
        List<Observable<String>> iter = new ArrayList<>();
        iter.add(Observable.interval(50, TimeUnit.MILLISECONDS)
                .take(stringList.size())
                .map(aLong -> stringList.get(aLong.intValue())));
        iter.add(Observable.interval(140, TimeUnit.MILLISECONDS)
                .take(integerList.size())
                .map(aLong -> integerList.get(aLong.intValue())));
        final DisposableObserver<String> disposableObserver = Observable.amb(iter).subscribeWith(new DisposableObserver<String>() {
            @Override
            public void onNext(String s) {
                log("onNext " + s);
            }

            @Override
            public void onError(Throwable e) {
                log("onError " + e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });


        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposeObservable(disposableObserver);
        }

    }

    @Test
    public void rxJavaMergeWithError() {
        final DisposableObserver<String> disposableObserver = Observable
                .merge(Observable.interval(50, TimeUnit.MILLISECONDS)
                                .flatMap(aLong -> aLong == stringList.size() - 2 ? Observable.error(new RuntimeException("Forced error")) : Observable.just(aLong))
                                .take(stringList.size())
                                .map(aLong -> stringList.get(aLong.intValue()))
                        ,
                        Observable.interval(40, TimeUnit.MILLISECONDS)
                                .take(integerList.size())
                                .map(aLong -> integerList.get(aLong.intValue())))
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        log("onNext " + s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        log("onError " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposeObservable(disposableObserver);
        }

    }

    @Test
    public void rxRetry() {
        final DisposableObserver<Double> disposableObserver = Observable.fromArray(1, 5, 7, 0, 2, 4).map(integer -> (double) (15 / integer)).retry(1).subscribeWith(new DisposableObserver<Double>() {
            @Override
            public void onNext(Double d) {
                log("onError " + d);
            }

            @Override
            public void onError(Throwable e) {
                log("onError " + e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposeObservable(disposableObserver);
        }
    }

    @Test
    public void rxOnError() {
        final DisposableObserver<Double> disposableObserver = Observable.fromArray(1, 5, 7, 0, 2, 4)
                .map(integer -> (double) (15 / integer))
                .onErrorReturnItem(100.0)
                .subscribeWith(new DisposableObserver<Double>() {
                    @Override
                    public void onNext(Double d) {
                        log("onError " + d);
                    }

                    @Override
                    public void onError(Throwable e) {
                        log("onError " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposeObservable(disposableObserver);
        }
    }

    @Test
    public void rxZipDiferrentCount() {
        final DisposableObserver<Integer> disposableObserver = Observable
                .zip(Observable.fromArray("1", "2", "3"), Observable.fromArray(4, 5, 6, 7), (s, integer) -> Integer.valueOf(s) + integer)
                .subscribeWith(new DisposableObserver<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        log("onNext " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        log("onError " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        log("onCompleate");
                    }
                });
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposeObservable(disposableObserver);
        }
    }

    @Test
    public void rxZip() {
        final DisposableObserver<Integer> disposableObserver = Observable.zip(Observable.fromArray("1", "2", "3"), Observable.fromArray(4, 5, 6), (s, integer) -> Integer.valueOf(s) + integer)
                .subscribeWith(new DisposableObserver<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        log("onNext " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        log("onError " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposeObservable(disposableObserver);
        }
    }

    @Test
    public void rxZipError() {
        final DisposableObserver<String> disposableObserver = Observable
                .zip(Observable.fromArray("1", "2", "3"), Observable.empty(), (s, o) -> s + o.toString())
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        log("onNext " + s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        log("onError " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        log("onCompleate");
                    }
                });
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposeObservable(disposableObserver);
        }
    }

    @Test
    public void rxZipOnDifferentThreads() {
        final DisposableObserver<Integer> disposableObserver = Observable
                .zip(Observable.fromArray("1", "2", "3").map(s -> s + "1").doOnNext(s -> log(s)).subscribeOn(Schedulers.computation()),
                        Observable.fromArray(4, 5, 6).map(integer -> integer * 2).doOnNext(integer -> log(integer)).subscribeOn(Schedulers.computation()), (s, integer) -> Integer.valueOf(s) + integer)
                .observeOn(Schedulers.newThread())
                .subscribeWith(new DisposableObserver<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        log("onNext " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        log("onError " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposeObservable(disposableObserver);
        }
    }

    /**
     * rxflatMap operator
     */
    @Test
    public void rxflatMap1() {
        final DisposableObserver<Integer> disposable = Observable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return someLongOperationThatReturnInteger();
            }
        }).repeat().take(2).subscribeWith(new DisposableObserver<Integer>() {
                                              @Override
                                              public void onNext(Integer o) {
                                                  log("onNext " + o);
                                              }

                                              @Override
                                              public void onError(Throwable e) {
                                                  log("onError " + e.getMessage());
                                              }

                                              @Override
                                              public void onComplete() {
                                                  log("onCompleate");
                                              }
                                          }
        );
        disposeObservable(disposable);
    }

    @Test
    public void rxflatMap2() {
        final DisposableObserver<Integer> disposable = Observable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return someLongOperationThatReturnInteger();
            }
        }).subscribeOn(Schedulers.io()).repeat().take(2).subscribeWith(new DisposableObserver<Integer>() {
                                                                           @Override
                                                                           public void onNext(Integer o) {
                                                                               log("onNext " + o);
                                                                           }

                                                                           @Override
                                                                           public void onError(Throwable e) {
                                                                               log("onError " + e.getMessage());
                                                                           }

                                                                           @Override
                                                                           public void onComplete() {
                                                                               log("onCompleate");
                                                                           }
                                                                       }
        );
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposeObservable(disposable);
        }
    }

    @Test
    public void rxflatMap3() {
        final DisposableObserver<Integer> disposable = Observable
                .fromCallable(() -> someLongWebServiceOperationThatReturnIntegerList())
                .flatMapIterable(integers -> integers)
                .subscribeWith(new DisposableObserver<Integer>() {
                                   @Override
                                   public void onNext(Integer o) {
                                       log("onNext " + o);
                                   }

                                   @Override
                                   public void onError(Throwable e) {
                                       log("onError " + e.getMessage());
                                   }

                                   @Override
                                   public void onComplete() {
                                       log("onCompleate");
                                   }
                               }
                );
        disposeObservable(disposable);
    }

    @Test
    public void rxflatMap4() {
        final DisposableObserver<Integer> disposable = Observable
                .fromCallable(() -> someLongWebServiceOperationThatReturnIntegerList())
                .flatMapIterable(integers -> integers)
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<Integer>() {
                                   @Override
                                   public void onNext(Integer o) {
                                       log("onNext " + o);
                                   }

                                   @Override
                                   public void onError(Throwable e) {
                                       log("onError " + e.getMessage());
                                   }

                                   @Override
                                   public void onComplete() {
                                       log("onCompleate");
                                   }
                               }
                );
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposeObservable(disposable);
        }
    }

    @Test
    public void rxflatMap5() {
        final DisposableObserver<Integer> disposable = Observable
                .fromCallable(() -> someLongWebServiceOperationThatReturnIntegerList())
                .flatMapIterable(integers -> integers)
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<Integer>() {
                                   @Override
                                   public void onNext(Integer o) {
                                       log("onNext " + o);
                                   }

                                   @Override
                                   public void onError(Throwable e) {
                                       log("onError " + e.getMessage());
                                   }

                                   @Override
                                   public void onComplete() {
                                       log("onCompleate");
                                   }
                               }
                );
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposeObservable(disposable);
        }
    }

    @Test
    public void rxflatMap6() {
        final DisposableObserver<Integer> disposable = Observable
                .fromCallable(() -> someLongWebServiceOperationThatReturnIntegerList())
                .flatMapIterable(integers -> integers)
                .flatMap(integer -> someLongOperationThatReturnObservable(integer))
                .subscribeWith(new DisposableObserver<Integer>() {
                                   @Override
                                   public void onNext(Integer o) {
                                       log("onNext " + o);
                                   }

                                   @Override
                                   public void onError(Throwable e) {
                                       log("onError " + e.getMessage());
                                   }

                                   @Override
                                   public void onComplete() {
                                       log("onCompleate");
                                   }
                               }
                );
//        try {
//            Thread.sleep(7000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } finally {
//        }
        disposeObservable(disposable);
    }

    @Test
    public void rxflatMap7() {
        final DisposableObserver<Integer> disposable = Observable
                .fromCallable(() -> someLongWebServiceOperationThatReturnIntegerList())
                .flatMapIterable(integers -> integers)
                .flatMap(integer -> someLongOperationThatReturnObservable(integer))
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<Integer>() {
                                   @Override
                                   public void onNext(Integer o) {
                                       log("onNext " + o);
                                   }

                                   @Override
                                   public void onError(Throwable e) {
                                       log("onError " + e.getMessage());
                                   }

                                   @Override
                                   public void onComplete() {
                                       log("onCompleate");
                                   }
                               }
                );
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposeObservable(disposable);
        }
    }

    @Test
    public void rxflatMap8() {
        final DisposableObserver<Integer> disposable = Observable
                .fromCallable(() -> someLongWebServiceOperationThatReturnIntegerList())
                .subscribeOn(Schedulers.io())
                .flatMapIterable(integers -> integers)
                .flatMap(integer -> someLongOperationThatReturnObservable(integer))
                .subscribeWith(new DisposableObserver<Integer>() {
                                   @Override
                                   public void onNext(Integer o) {
                                       log("onNext " + o);
                                   }

                                   @Override
                                   public void onError(Throwable e) {
                                       log("onError " + e.getMessage());
                                   }

                                   @Override
                                   public void onComplete() {
                                       log("onCompleate");
                                   }
                               }
                );
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposeObservable(disposable);
        }
    }

    @Test
    public void rxflatMap9() {
        final DisposableObserver<Integer> disposable = Observable
                .fromCallable(this::someLongWebServiceOperationThatReturnIntegerList)
                .subscribeOn(Schedulers.io())
                .flatMapIterable(integers -> integers)
                .flatMap(this::someLongOperationThatReturnObservableConcurrent)
                .subscribeWith(new DisposableObserver<Integer>() {
                                   @Override
                                   public void onNext(Integer o) {
                                       log("onNext " + o);
                                   }

                                   @Override
                                   public void onError(Throwable e) {
                                       log("onError " + e.getMessage());
                                   }

                                   @Override
                                   public void onComplete() {
                                       log("onCompleate");
                                   }
                               }
                );
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposeObservable(disposable);
        }
    }

    @Test
    public void rxflatMap10() {
        final DisposableObserver<Integer> disposable = Observable
                .fromCallable(this::someLongWebServiceOperationThatReturnIntegerList)
                .subscribeOn(Schedulers.io())
                .flatMapIterable(integers -> integers)
                .flatMap(this::someLongOperationThatReturnObservableConcurrentDelay)
                .subscribeWith(new DisposableObserver<Integer>() {
                                   @Override
                                   public void onNext(Integer o) {
                                       log("onNext " + o);
                                   }

                                   @Override
                                   public void onError(Throwable e) {
                                       log("onError " + e.getMessage());
                                   }

                                   @Override
                                   public void onComplete() {
                                       log("onCompleate");
                                   }
                               }
                );
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposeObservable(disposable);
        }
    }


    @Test
    public void rxJavaConcatAvailability() {
        final DisposableObserver<String> disposableObserver = Observable
                .concat(Observable.timer(5, TimeUnit.MILLISECONDS)
                                .map(aLong -> "FIRST")
                        ,
                        Observable
                                .timer(4500, TimeUnit.MILLISECONDS)
                                .map(aLong -> "SECOND"))
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        log("onNext " + s);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposeObservable(disposableObserver);
        }
    }

    @Test
    public void rxJavaConcatConnectableObservable() {
        final ConnectableObservable<Long> sharedObservable = Observable.interval(1000, TimeUnit.MILLISECONDS).publish();
        final DisposableObserver<String> disposableObserver1 = Observable.concat(
                //interesting effect,subscription on outer concat observable is immediate
                //but shared(Connectable)observable subscription is delayed till connect is called
                //so there is no emission of events
                Observable.just("this is immediatelyemittedd"),
                sharedObservable
                        .take(1)
                        .map(aLong -> aLong + "-" + Thread.currentThread().getName())
                , sharedObservable
                        .take(6)
                        .concatMapEager(aLong -> Observable.just(aLong).map(aLong1 -> aLong1 + "-" + Thread.currentThread().getName())
                                .subscribeOn(Schedulers.computation()))
        )
                .observeOn(Schedulers.newThread())
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String aLong) {
                        log(aLong);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final Disposable disposable = sharedObservable.connect();


        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposeObservable(disposableObserver1);
            disposable.dispose();
        }
    }

    /**
     * this can be more elegant solution to WA precipitation problem
     * for downloading first image with full bandwidth
     * and then continue with rest in parallel
     * after first is finished
     */
    @Test
    public void rxJavaConcatPublish() {
        final DisposableObserver<String> disposableObserver = Observable
                .interval(1000, TimeUnit.MILLISECONDS)
                .publish(longObservable -> Observable.concat(
                        longObservable
                                .take(1)
                                .map(aLong -> aLong + "-" + Thread.currentThread().getName()),
                        longObservable
                                //.delay(2000,TimeUnit.MILLISECONDS)//this is added just to be sure that delay don't change anything
                                .take(6)
                                .concatMapEager(aLong -> Observable.just(aLong).map(aLong1 -> aLong1 + "-" + Thread.currentThread().getName()).subscribeOn(Schedulers.newThread()))))//newThread() spit tread for every computation
//                              .concatMapEager(aLong -> Observable.just(aLong).map(aLong1 -> aLong1 + "-" + Thread.currentThread().getName()).subscribeOn(Schedulers.io()))))//io() spit tread for every computation, so be careful when using inside xxxMap() operators
//                              .concatMapEager(aLong -> Observable.just(aLong).map(aLong1 -> aLong1 + "-" + Thread.currentThread().getName()).subscribeOn(Schedulers.computation()))))//computation calculate number of cores internally and allocate appropriate number of thread that are <= numCores
                .observeOn(Schedulers.newThread())
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        log(s);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        try {
            Thread.sleep(12000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposeObservable(disposableObserver);
        }
    }

    /**
     * work in progress
     */
    @Test
    public void testPublishRefCount() {
        final Observable<Long> longObservable = Observable.interval(1, TimeUnit.SECONDS).take(4).publish().refCount();
//        final Observable<Long> longObservable = Observable.create(new ObservableOnSubscribe<Long>() {
//            @Override
//            public void subscribe(ObservableEmitter<Long> e) throws Exception {
//                e.onNext(1L);
//                e.onNext(2L);
//                e.onNext(3L);
//                e.onNext(4L);
//                e.onComplete();
//            }
//        }).replay(1).refCount();
        final DisposableObserver<Long> disposableObserver = longObservable.subscribeOn(Schedulers.newThread()).doOnSubscribe(disposable -> log("First subscribed")).subscribeWith(new DisposableObserver<Long>() {
            @Override
            public void onNext(Long integer) {
                log(integer);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        //sleep for 2 sec and subscribe again
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        final DisposableObserver<Long> disposableObserver1 = longObservable.subscribeOn(Schedulers.newThread()).doOnSubscribe(disposable -> log("Second subscribed")).subscribeWith(new DisposableObserver<Long>() {
            @Override
            public void onNext(Long integer) {
                log(integer);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
        //wait for rxjava to finish
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposeObservable(disposableObserver);
            disposeObservable(disposableObserver1);

        }

    }

    /**
     * work in progress
     */
    @Test
    public void testReplay() {
        final Observable<Long> longObservable = Observable.interval(1, TimeUnit.SECONDS).take(4).replay(1).refCount();
//        final Observable<Long> longObservable = Observable.create(new ObservableOnSubscribe<Long>() {
//            @Override
//            public void subscribe(ObservableEmitter<Long> e) throws Exception {
//                e.onNext(1L);
//                e.onNext(2L);
//                e.onNext(3L);
//                e.onNext(4L);
//                e.onComplete();
//            }
//        }).replay(1).refCount();
        final DisposableObserver<Long> disposableObserver = longObservable.subscribeOn(Schedulers.newThread()).doOnSubscribe(disposable -> log("First subscribed")).subscribeWith(new DisposableObserver<Long>() {
            @Override
            public void onNext(Long integer) {
                log(integer);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        //sleep for 2 sec and subscribe again
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        final DisposableObserver<Long> disposableObserver1 = longObservable.subscribeOn(Schedulers.newThread()).doOnSubscribe(disposable -> log("Second subscribed")).subscribeWith(new DisposableObserver<Long>() {
            @Override
            public void onNext(Long integer) {
                log(integer);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
        //wait for rxjava to finish
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposeObservable(disposableObserver);
            disposeObservable(disposableObserver1);

        }

    }

    /**
     * this test is still in progress
     * not working as expected
     * BROKEN IMPLEMENTATION
     * ignore for now
     */
    @Test
    public void testRetryWhen() {
        final DisposableObserver<Object> disposableObserver = Observable.fromCallable(() -> {
            log("Loading ...");
            Thread.sleep(2000);
            throw new NoSuchFieldException();
        }).retryWhen(throwableFlowable -> (ObservableSource<?>) Observable.zip(throwableFlowable, Observable.range(1, 2),
                (throwable, u) ->
                        u)
                .flatMap(u -> {
                    if (u < 2) {
                        return Observable.just(u);
                    } else {
                        return Observable.error(new Throwable("Error"));
                    }
                })).subscribeWith(new DisposableObserver<Object>() {
            @Override
            public void onNext(Object o) {
                log(o.toString());
            }

            @Override
            public void onError(Throwable e) {
                log(e.getMessage());
            }

            @Override
            public void onComplete() {
                log("Compleated");
            }
        });
        disposeObservable(disposableObserver);
    }

    class SessionTokenExpieredException extends RuntimeException {
        @Override
        public String getMessage() {
            return "Session token expiered";
        }

        @Override
        public String getLocalizedMessage() {
            return "Session token expiered";
        }
    }

    private static final int NUM_RETRYS_COUNT = 1;

    private static ObservableSource<?> retryOneTime(Observable<Throwable> throwableObservable) {
        return throwableObservable
                .flatMap(new Function<Throwable, ObservableSource<?>>() {
                    int count = 0;

                    @Override
                    public ObservableSource<?> apply(Throwable throwable) throws Exception {
                        if (count++ < NUM_RETRYS_COUNT && throwable instanceof SessionTokenExpieredException) {
                            //here we can hit refresh token WS call and retry api call againg
                            //with new token
                            return Observable.just(count);
                        }
                        return Observable.error(throwable);
                    }
                });
    }

    private static ObservableSource<?> retryOneTimeModified(Observable<Throwable> throwableObservable, AtomicInteger integer) {
        return throwableObservable
                .flatMap(new Function<Throwable, ObservableSource<?>>() {
                    int count = 0;

                    @Override
                    public ObservableSource<?> apply(Throwable throwable) throws Exception {
                        if (count++ < NUM_RETRYS_COUNT && throwable instanceof SessionTokenExpieredException) {
                            //here we can hit refresh token WS call and retry api call againg
                            //with new token
                            integer.incrementAndGet();
                            return Observable.just(count);
                        }
                        return Observable.error(throwable);
                    }
                });
    }

    /**
     * This is right way to
     * retry and can be used to refresh token and continue
     * with ongoing api call that failed because
     * of session token expiration
     */
    @Test
    public void testRetryWhenSecondAttempt() {
        final DisposableObserver<Object> disposableObserver = Observable.fromCallable(() -> {
            log("Loading data from server ...");
            Thread.sleep(2000);
            //throw exception on purpose here
            //to simulate token expiration
            throw new SessionTokenExpieredException();
        })
                //.map(response -> {//check here in case webservice returns expiration token
                // inside successful response
                //in that case delete above throw expression
                // throw exception from here like this
                // Exception.propagate(throw new SessionTokenExpieredException());})
                .retryWhen(ExampleUnitTest::retryOneTime)
                .subscribeWith(new DisposableObserver<Object>() {
                    @Override
                    public void onNext(Object o) {
                        log(o);
                    }

                    @Override
                    public void onError(Throwable e) {
                        log(e);
                    }

                    @Override
                    public void onComplete() {
                        log("Compleated");
                    }
                });
        disposeObservable(disposableObserver);
    }


    /**
     *
     */
    @Test
    public void testPublishRefCountCallable() {
        final Observable<Long> longObservable = longRunningWsOrComputation()
                .publish()
                .refCount();
        final DisposableObserver<Long> disposableObserver = longObservable
                .subscribeOn(Schedulers.newThread())
                .doOnSubscribe(disposable -> log("First subscribed"))
                .subscribeWith(new DisposableObserver<Long>() {
                    @Override
                    public void onNext(Long integer) {
                        log(integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        //sleep for 2 sec and subscribe again
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        final DisposableObserver<Long> disposableObserver1 = longObservable
                .subscribeOn(Schedulers.newThread())
                .doOnSubscribe(disposable -> log("Second subscribed"))
                .subscribeWith(new DisposableObserver<Long>() {
                    @Override
                    public void onNext(Long integer) {
                        log(integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        //wait for rxjava to finish
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposeObservable(disposableObserver);
            disposeObservable(disposableObserver1);

        }

    }

    private Observable<Long> longRunningWsOrComputation() {
        return Observable.fromCallable(() -> {
            log("Loading...");
            Thread.sleep(2000);
            return 100L;
        });
    }

    private Single<Long> longRunningWsOrComputationSingle() {
        return Single.fromCallable(() -> {
            log("Loading...");
            Thread.sleep(2000);
            return 100L;
        });
    }

    /**
     *
     */
    @Test
    public void testReplayCallable() {
        final Observable<Long> longObservable = longRunningWsOrComputation()
                .replay(1)
                .refCount();
        final DisposableObserver<Long> disposableObserver = longObservable
                .subscribeOn(Schedulers.newThread())
                .doOnSubscribe(disposable -> log("First subscribed"))
                .subscribeWith(new DisposableObserver<Long>() {
                    @Override
                    public void onNext(Long integer) {
                        log(integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        //sleep for 2 sec and subscribe again
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        final DisposableObserver<Long> disposableObserver1 = longObservable
                .subscribeOn(Schedulers.newThread())
                .doOnSubscribe(disposable -> log("Second subscribed"))
                .subscribeWith(new DisposableObserver<Long>() {
                    @Override
                    public void onNext(Long integer) {
                        log(integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        //wait for rxjava to finish
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposeObservable(disposableObserver);
            disposeObservable(disposableObserver1);

        }

    }

    /**
     * Test that show using auto connect in combination with replay
     * When first observable finish and unsubscribe and second
     * connect we get last value that is emitted
     * In case of auto connect we need to save reference to disposable
     * so we can release in the end
     */

    @Test
    public void testReplayAutoconnect() {
        //wraper for waribale that we access inside
        //lambda(anonymous class) that should be final
        AtomicReference<Disposable> topDisposable = new AtomicReference<>();
        final Observable<Long> longObservable = Observable.just(1L, 2L, 3L, 4L, 5L)
                .replay(1)
                .autoConnect(1, topDisposable::set);
        final DisposableObserver<Long> disposableObserver = longObservable
                .doOnSubscribe(disposable -> log("First subscribed"))
                .subscribeWith(new DisposableObserver<Long>() {
                    @Override
                    public void onNext(Long integer) {
                        log(integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        //sleep for 2 sec and subscribe again
        //in this case this is not necessarry
        //because all operation are on the same thread
        //so there are executed sequentialy
        //but we do it here just for reference
        //commenting below part of code will not
        //change anything
//        try {
//            Thread.sleep(1500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


        final DisposableObserver<Long> disposableObserver1 = longObservable
                .doOnSubscribe(disposable -> log("Second subscribed"))
                .subscribeWith(new DisposableObserver<Long>() {
                    @Override
                    public void onNext(Long integer) {
                        log(integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        disposeObservable(disposableObserver);
        disposeObservable(disposableObserver1);
        topDisposable.get().dispose();
    }

    /**
     * In this case we simulate long running
     * WS call,after call finish first observable
     * unsubscribe,then second connect after some
     * time,maybe even from another part of app
     * we emit last response without
     * executing again expensive WS call
     * (or other type of long running operation)
     */

    @Test
    public void testReplayAutoconnectCallabell() {
        AtomicReference<Disposable> disposableAtomicReference = new AtomicReference<>();
        final Observable<Integer> integerObservable = Observable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log("Loading WS data...");
                Thread.sleep(2500);
                return 42;
            }
        })
                .replay(1)
                .autoConnect(1, disposableAtomicReference::set);

        final DisposableObserver<Integer> first_subscribed = integerObservable
                .doOnSubscribe(disposable -> log("First subscribed"))
                .subscribeWith(new DisposableObserver<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        log(integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        final DisposableObserver<Integer> second_subscribed = integerObservable
                .doOnSubscribe(disposable -> log("Second subscribed"))
                .subscribeWith(new DisposableObserver<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        log(integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        disposeObservable(first_subscribed);
        disposeObservable(second_subscribed);
        disposableAtomicReference.get().dispose();

    }

    /**
     * Same as above but we reconnect againg
     * after last emitted event is returned
     * Can be used to draw UI with old data first
     * and then refresh it with new when WS
     * finish execution
     */

    @Test
    public void testReplayingShareReconnect() {
        //this should be some sort of global level variable(sort of singleton)
        final Observable<Long> longObservable = longRunningWsOrComputation()
                .compose(ReplayingShare.instance());

        //this is called from different parts of app
        final DisposableObserver<Long> disposableObserver = longObservable
                .subscribeOn(Schedulers.newThread())
                .doOnSubscribe(disposable -> log("First subscribed"))
                .subscribeWith(new DisposableObserver<Long>() {
                    @Override
                    public void onNext(Long integer) {
                        log(integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        //sleep for 2 sec so that first observable is completed(finished)
        //and after that subscribe again in different part of app
        //we will get cached result of first call
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //dispose(unsubscribe) from first observable
        disposeObservable(disposableObserver);

        //this is called from different parts of app
        //now we call it again,in this case we will get cached result
        //first and then we execute WS call again to get fresh data
        final DisposableObserver<Long> disposableObserver1 = longObservable
                .subscribeOn(Schedulers.newThread())
                .doOnSubscribe(disposable -> log("Second subscribed"))
                .subscribeWith(new DisposableObserver<Long>() {
                    @Override
                    public void onNext(Long integer) {
                        log(integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        //wait for rxjava to finish
        //or remove all subscribeOn()
        //operator to get all calls on same tread
        //executed sequentialy,in that
        //case we can remove all  Thread.sleep() calls
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposeObservable(disposableObserver1);
        }

    }

    /**
     * Interesting case for sharing result using ReplayingShare()
     * library for the same operation
     * that is called from different part of app in no
     * particular order across app with only one
     * execution of expensive operation
     * applicable to Observable and Flowablle
     * This have same behavior as
     * test @testReplayAutoconnect()
     */
    @Test
    public void testReplayingShare() {
        //this is app level variable(sort of singleton)
        final Observable<Long> longObservable = longRunningWsOrComputation()
                .compose(ReplayingShare.instance());

        //this is called from different parts of app
        final DisposableObserver<Long> disposableObserver = longObservable
                .take(1)//important part to skip computation after first subscriber
                .subscribeOn(Schedulers.newThread())
                .doOnSubscribe(disposable -> log("First subscribed"))
                .subscribeWith(new DisposableObserver<Long>() {
                    @Override
                    public void onNext(Long integer) {
                        log(integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        //sleep for 2 sec so that first observable is completed(finished)
        //and after that subscribe again in different part of app
        //we will get cached result of first call and slow operation is not
        //called again
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //dispose(unsubscribe) from first observable
        disposeObservable(disposableObserver);

        //this is called from different parts of app
        //now we call it again,in this case we will get cached result
        //long operation is skipped
        final DisposableObserver<Long> disposableObserver1 = longObservable
                .take(1)//important part to skip computation after first subscriber
                .subscribeOn(Schedulers.newThread())
                .doOnSubscribe(disposable -> log("Second subscribed"))
                .subscribeWith(new DisposableObserver<Long>() {
                    @Override
                    public void onNext(Long integer) {
                        log(integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        //wait for rxjava to finish
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposeObservable(disposableObserver1);
        }

    }

    /**
     * This is right way to
     * retry and can be used to refresh token and continue
     * with ongoing api call that failed because
     * of session token expiration
     * Here we simulate successful retrieval
     * of refresh token and proceed normally
     */
    @Test
    public void testRetryWhenSecondAttemptWithSuccess() {
        AtomicInteger atomicInteger = new AtomicInteger(42);
        final DisposableObserver<Object> disposableObserver = Observable.fromCallable(() -> {
            log("Loading data from server ...");
            Thread.sleep(2000);
            //throw exception here
            //to simulate token expiration
            //if we are on initial api call
            if (atomicInteger.get() == 42) {
                log("Session token expiered");
                throw new SessionTokenExpieredException();
            }
            log("Loading data succedeed afer token is refreshed");
            log("Loaded data is");
            return atomicInteger.get();
        })
                //.map(response -> {//check here in case webservice returns expiration token
                // inside successful response
                //in that case delete above throw expression
                // throw exception from here like this
                // Exception.propagate(throw new SessionTokenExpieredException());})
                .retryWhen(throwableObservable -> throwableObservable
                        .flatMap(new Function<Throwable, ObservableSource<?>>() {
                            int count = 0;

                            @Override
                            public ObservableSource<?> apply(Throwable throwable) throws Exception {
                                if (count++ < NUM_RETRYS_COUNT && throwable instanceof SessionTokenExpieredException) {
                                    //here we can hit refresh token WS call and retry api call again
                                    //with new token,in this case we modify external
                                    //variable thah simulate refresh token
                                    //it's not important wath we return here
                                    //what is important is that if we need to retry
                                    //WS call we need to return something but not error or completed event
                                    return Observable.just(atomicInteger.incrementAndGet());
                                }
                                //in case we don't wont to retry again, just propagate error downstream
                                return Observable.error(throwable);
                            }
                        }))
                .subscribeWith(new DisposableObserver<Object>() {
                    @Override
                    public void onNext(Object o) {
                        log(o);
                    }

                    @Override
                    public void onError(Throwable e) {
                        log(e);
                    }

                    @Override
                    public void onComplete() {
                        log("Compleated");
                    }
                });
        disposeObservable(disposableObserver);
    }

    /**
     * Just testing thread change inside create method
     */
    @Test
    public void testCreateBackgroundProccesing() {
        final Disposable disposable = Observable.create(e -> {
            try {
                for (int i = 0; i < 6; i++) {
                    if (e.isDisposed())
                        break;
                    e.onNext(i);
                    log(i);
                }
                e.onComplete();
            } catch (Exception e1) {
                e.onError(e1);
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.single())
                .subscribe(o -> {
                    log(o);
                }, throwable -> log(throwable.getMessage()), () -> {
                });
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposable.dispose();
        }

    }

    /**
     * Just testing flowable generate tread switching inside generate method
     */
    @Test
    public void testFlowableGenerateBackgroundProccesing() {
        final Disposable disposable = Flowable.generate(() -> new AtomicInteger(20), (integer, emitter) -> {
            final int i = integer.decrementAndGet();
            if (i > 0) {
                emitter.onNext(integer.get());
                log(i);
            } else {
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.single())
                .subscribe(o -> {
                    log(o);
                }, throwable -> {
                    log(throwable.getMessage());
                }, () -> {
                    log("Compleated");
                });

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            disposable.dispose();
        }

    }

}