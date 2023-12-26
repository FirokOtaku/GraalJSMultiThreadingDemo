package firok.demo.graaljsthreadpool;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.intellij.lang.annotations.Language;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainCallJavaMethod
{
    /**
     * any blocking method
     * */
    public static void anyRandomBlockingMethod()
    {
        var rand = new Random();
        try(var pool = Executors.newFixedThreadPool(8))
        {
            for(var step = 0; step < 10; step++)
            {
                final var stepFinal = step;
                pool.submit(() -> {
                    System.out.println("child thread start: " + stepFinal);

                    try { Thread.sleep(rand.nextInt(5) + 1); }
                    catch (InterruptedException e) { e.printStackTrace(); }

                    System.out.println("child thread end: " + stepFinal);
                });
            }
            pool.shutdown();
            pool.awaitTermination(12, TimeUnit.SECONDS);
        }
        catch (Exception any)
        {
            throw new RuntimeException("script execution failed", any);
        }
    }

    public static void main(String[] args) throws Exception
    {
        var context = Context.newBuilder("js").allowAllAccess(true).build();
        // put method into JavaScript context
        context.getBindings("js").putMember("anyRandomBlockingMethod", (Runnable) MainCallJavaMethod::anyRandomBlockingMethod);
        @Language("JS")
        var scriptContent = """
                const { log } = console
                log('script start')
                anyRandomBlockingMethod()
                log('script end')
                """;
        var source = Source.newBuilder("js", scriptContent, "script.js").build();
        context.eval(source);
    }
}
