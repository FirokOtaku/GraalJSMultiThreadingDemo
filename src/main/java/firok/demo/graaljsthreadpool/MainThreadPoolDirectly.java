package firok.demo.graaljsthreadpool;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.intellij.lang.annotations.Language;

/**
 * create Thread Pool and submit task on it directly
 * */
public class MainThreadPoolDirectly
{
    public static void main(String[] args)
    {
        @SuppressWarnings("JSUnresolvedReference") @Language("JS")
        var scriptContent = """
                    const Executors = Java.type('java.util.concurrent.Executors')
                    const Runnable = Java.extend(Java.type('java.lang.Runnable'))
                    const TimeUnit = Java.type('java.util.concurrent.TimeUnit')
                    const Thread = Java.type('java.lang.Thread')
                    const AtomicInteger = Java.type('java.util.concurrent.atomic.AtomicInteger')
                    const { log } = console
                    
                    const counter = new AtomicInteger(0)
                    const pool = Executors.newFixedThreadPool(6)
                    for(let step = 0; step < 10; step++)
                    {
                        log('commit child thread', step)
                        
                        pool.execute(new Runnable(function () {
                            const timeWait = parseInt('' + Math.random() * 4) + 1
                            log('child thread start', step, timeWait)
                            Thread.sleep(timeWait * 1000)
                            log('child thread end', step, counter.incrementAndGet())
                        }))
                    }
                    pool.shutdown()
                    pool.awaitTermination(1, TimeUnit.MINUTES)
                    pool.close()
                    
                    log('main thread end', pool, counter.get())
                    """;
        try(var context = Context.newBuilder("js").allowCreateThread(true).allowAllAccess(true).build())
        {
            var source = Source.newBuilder("js", scriptContent, "main.js").build();
            context.eval(source);
        }
        catch (Exception any)
        {
            System.err.println("failed to execute script");
            any.printStackTrace(System.err);
        }
    }
}
