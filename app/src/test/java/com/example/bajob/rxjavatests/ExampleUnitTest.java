package com.example.bajob.rxjavatests;

import org.junit.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.internal.schedulers.IoScheduler;
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
    public void rxZipError() {
        final DisposableObserver<String> disposableObserver = Observable
                .zip(Observable.fromArray("1", "2", "3"), Observable.empty(),(s, o) -> s+o.toString())
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
                .zip(Observable.fromArray("1", "2", "3").map(s -> s+"1").doOnNext(s -> log(s)).subscribeOn(Schedulers.computation()),
                        Observable.fromArray(4, 5, 6).map(integer -> integer*2).doOnNext(integer -> log(integer)).subscribeOn(Schedulers.computation()), (s, integer) -> Integer.valueOf(s) + integer)
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
}