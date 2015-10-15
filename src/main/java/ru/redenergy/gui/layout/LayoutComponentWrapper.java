package ru.redenergy.gui.layout;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.internal.UnsafeAllocator;

import ru.redenergy.gui.component.IGuiComponent;
import ru.redenergy.gui.layout.argument.ILayoutArgument;
import ru.redenergy.gui.layout.argument.LayoutArgument;
import ru.redenergy.gui.layout.argument.LayoutCalculatableArgument;
import ru.redenergy.gui.show.IShow;

import sun.reflect.*;

public class LayoutComponentWrapper {
    private Class type;
    private Set<ILayoutArgument> args;
    
    public LayoutComponentWrapper(Class type, Set<ILayoutArgument> args){
        this.type = type;
        this.args = args;
    }
    
    public Class getType(){
        return type;
    }
    
    public Set<ILayoutArgument> getArguments(){
        return args;
    }
    
    public IGuiComponent create() throws Exception{
        return create(null);
    }
    
    public IGuiComponent create(IShow show) throws Exception{
        IGuiComponent com;
        
        com = instantiateType(type);
        for(ILayoutArgument arg : args){
            Object value = null;
            if(arg instanceof LayoutArgument){
                value = ((LayoutArgument) arg).get();
            } else if(arg instanceof LayoutCalculatableArgument && show != null){
                value = ((LayoutCalculatableArgument) arg).get(Pair.of("width", show.getWidth()), Pair.of("height", show.getHeight()));
            }
            
            FieldUtils.writeField(com, arg.fieldName(), value, true);
        }
        return com;
    }
    
    private static IGuiComponent instantiateType(Class type){
        try {
            Constructor constr = type.getDeclaredConstructor();
            return (IGuiComponent) constr.newInstance();
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException("Can't instantiate " + type.getName() + " with zero-arg constructor");
        }
    }
}
