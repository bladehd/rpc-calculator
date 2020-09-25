package com.tony.challenge.calculator.shell;

import com.tony.challenge.calculator.exception.ParameterNotSufficientException;
import org.jline.utils.Signals;
import org.springframework.core.MethodParameter;
import org.springframework.shell.*;
import org.springframework.util.ReflectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.nio.channels.ClosedByInterruptException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author tony.chen
 */
public class MultiCommandShell extends Shell {
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private static final String DEFAULT_METHOD = "default";

    public MultiCommandShell(ResultHandler resultHandler) {
        super(resultHandler);
    }

    private boolean noInput(Input input) {
        return input.words().isEmpty() || input.words().size() == 1 && ((String) input.words().get(0)).trim().isEmpty() || ((String) input.words().iterator().next()).matches("\\s*//.*");
    }

    /**
     * Modify to filter arguments between different commands
     *
     * @param command
     * @param words
     * @return
     */
    private List<String> wordsForArguments(String command, List<String> words) {
        int index = 0;
        for (int i = 0; i < words.size(); i++) {
            if (command.equals(words.get(i))) {
                index = i;
                break;
            }
        }
        List<String> args = words.subList(0, index);
        return args;
    }

    private Object[] resolveArgs(Method method, List<String> wordsForArgs) {
        List<MethodParameter> parameters = (List) Utils.createMethodParameters(method).collect(Collectors.toList());
        Object[] args = new Object[parameters.size()];
        Arrays.fill(args, UNRESOLVED);
        Iterator var5 = this.parameterResolvers.iterator();

        while (var5.hasNext()) {
            ParameterResolver resolver = (ParameterResolver) var5.next();

            for (int argIndex = 0; argIndex < args.length; ++argIndex) {
                MethodParameter parameter = (MethodParameter) parameters.get(argIndex);
                if (args[argIndex] == UNRESOLVED && resolver.supports(parameter)) {
                    args[argIndex] = resolver.resolve(parameter, wordsForArgs).resolvedValue();
                }
            }
        }

        return args;
    }

    private void validateArgs(Object[] args, MethodTarget methodTarget) {
        for (int i = 0; i < args.length; ++i) {
            if (args[i] == UNRESOLVED) {
                MethodParameter methodParameter = Utils.createMethodParameter(methodTarget.getMethod(), i);
                throw new IllegalStateException("Could not resolve " + methodParameter);
            }
        }

        Set<ConstraintViolation<Object>> constraintViolations = this.validator.forExecutables().validateParameters(methodTarget.getBean(), methodTarget.getMethod(), args, new Class[0]);
        if (constraintViolations.size() > 0) {
            throw new ParameterValidationException(constraintViolations, methodTarget);
        }
    }

    @Override
    public Object evaluate(Input input) {
        if (this.noInput(input)) {
            return NO_INPUT;
        } else {
            String line = ((String) input.words().stream().collect(Collectors.joining(" "))).trim();
            List<String> words = input.words();
            List<String> commands = this.findCommand(words);
            ;
            if (commands.size() == 0) {
                MethodTarget methodTarget = (MethodTarget) this.methodTargets.get(DEFAULT_METHOD);
                return ReflectionUtils.invokeMethod(methodTarget.getMethod(), methodTarget.getBean(), words);
            } else {
                Object result = null;
                words = new ArrayList<>(words);
                int pos = 0;
                for (String command : commands) {
                    if(pos == 0){
                        pos = line.indexOf(command);
                    }else{
                        pos = line.indexOf(command, pos + 1);
                    }

                    MethodTarget methodTarget = (MethodTarget) this.methodTargets.get(command);
                    Availability availability = methodTarget.getAvailability();
                    if (availability.isAvailable()) {
                        List<String> wordsForArgs = this.wordsForArguments(command, words);
                        words = words.subList(wordsForArgs.size() + 1, words.size());
                        Method method = methodTarget.getMethod();
                        Thread commandThread = Thread.currentThread();
                        Object sh = Signals.register("INT", () -> {
                            commandThread.interrupt();
                        });

                        Throwable var20 = null;
                        try {
//                            Object[] args = this.resolveArgs(method, wordsForArgs);//FIXME need to do dynamic argument number mapping
//                            this.validateArgs(args, methodTarget);
                            result = ReflectionUtils.invokeMethod(method, methodTarget.getBean(), new Object[]{wordsForArgs});
                        } catch (UndeclaredThrowableException var17) {
                            if (var17.getCause() instanceof InterruptedException || var17.getCause() instanceof ClosedByInterruptException) {
                                Thread.interrupted();
                            }

                            var20 = var17.getCause();
                            return var20;
                        } catch (Exception exception) {
                            //FIXME Just workaround to handle biz exception
                            if(exception instanceof ParameterNotSufficientException){
                                ((ParameterNotSufficientException) exception).setOperator(command);
                                ((ParameterNotSufficientException) exception).setPos(pos);
                            }
                            return exception;
                        } finally {
                            Signals.unregister("INT", sh);
                        }
                    } else {
                        return new CommandNotCurrentlyAvailable(command, availability);
                    }
                }
                return result;
            }
        }
    }

    /**
     * modify original find command logic, in this application, support multi operation command
     *
     * @param words
     * @return
     */
    private List<String> findCommand(List<String> words) {
        List<String> commands = new ArrayList<>();
        for (String word : words) {
            if (this.methodTargets.keySet().contains(word)) {
                commands.add(word);
            }
        }
        return commands;
//        String result = (String)this.methodTargets.keySet().stream().filter((command) -> {
//            return line.equals(command) || line.endsWith(" " + command);
//        }).reduce("", (c1, c2) -> {
//            return c1.length() > c2.length() ? c1 : c2;
//        });
//        return "".equals(result) ? null : result;
    }
}
