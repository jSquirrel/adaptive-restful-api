
package cz.cvut.fel.adaptiverestfulapi.example;

import cz.cvut.fel.adaptiverestfulapi.meta.ModelInspectionListener;
import cz.cvut.fel.adaptiverestfulapi.meta.model.*;
import cz.cvut.fel.adaptiverestfulapi.meta.model.Entity;
import cz.cvut.fel.adaptiverestfulapi.meta.reflection.Triplet;

import javax.persistence.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Set;


/**
 * The default model inspection listener.
 */
public class ModelListener implements ModelInspectionListener {

    @Override
    public Entity entity(Class clazz) {
        return new Entity(this.entityName(clazz), clazz);
    }

    @Override
    public Property property(Triplet<Field, Method, Method> triplet, Entity entity, Set<Entity> entities) {
        Method getter = triplet.b;
        Method setter = triplet.c;

        String shortName = this.propertyShortName(triplet, entity);
        String name = this.propertyName(triplet, entity);
        Entity targetEntity = this.targetEntity(triplet, entity, entities);

        if (targetEntity != null) {
            RelationshipType relationshipType = this.relationshipType(triplet, entity, entities);
            return new Relationship(name, shortName, getter, setter, targetEntity.getName(), relationshipType);
        }

        Type attributeType = this.attributeType(triplet, entity);
        boolean primary = this.isPrimary(triplet, entity);
        return new Attribute(name, shortName, getter, setter, attributeType, primary);
    }

    /**
     * Returns entity name from class.
     *
     * It uses fully qualified name of the class,
     * eq.: `com.app.model.Project`.
     *
     * @param clazz
     * @return The entity name.
     */
    protected String entityName(Class clazz) {
        return clazz.getName();
    }

    /**
     * Returns property name from triplet of field, getter and setter.
     *
     * It uses entity name combined with short name,
     * eq.: `com.app.model.Project.startedAt`.
     *
     * @param triplet
     * @param entity The entity of the property.
     * @return The property name.
     */
    protected String propertyName(Triplet<Field, Method, Method> triplet, Entity entity) {
        return entity.getName() + "." + this.propertyShortName(triplet, entity);
    }

    /**
     * Returns property short name from triplet of field, getter and setter.
     *
     * It uses entity name combined with name of the field,
     * or name of the getter without `get` (`is`) prefix,
     * or name of the setter without `set` prefix,
     * eq.: `startedAt`.
     *
     * @param triplet
     * @param entity The entity of the property.
     * @return The property short name.
     */
    protected String propertyShortName(Triplet<Field, Method, Method> triplet, Entity entity) {
        Field field = triplet.a;
        Method getter = triplet.b;
        Method setter = triplet.c;

        if (field != null) {
            return this.toFirstLetterLowerCase(field.getName());

        } else if (getter != null && getter.getName().startsWith("get"))  {
            return this.toFirstLetterLowerCase(getter.getName().substring("get".length()));

        } else if (getter != null && getter.getName().startsWith("is"))  {
            return this.toFirstLetterLowerCase(getter.getName().substring("is".length()));

        } else if (setter != null && setter.getName().startsWith("set"))  {
            return this.toFirstLetterLowerCase(setter.getName().substring("set".length()));

        } else {
            return null;
        }
    }

    /**
     * Looks for relationship type from triplet of field, getter and setter.
     * @param triplet
     * @param entity The entity of the property.
     * @param entities The known entities in the model.
     * @return The relationship type or null if not found.
     */
    protected RelationshipType relationshipType(Triplet<Field, Method, Method> triplet, Entity entity, Set<Entity> entities) {
        RelationshipType relationshipType = null;

        if (triplet.a != null) {
            relationshipType = this.relationshipTypeFromAnnotations(triplet.a.getAnnotations());
            if (relationshipType != null) {
                return relationshipType;
            }
        }

        if (triplet.b != null) {
            relationshipType = this.relationshipTypeFromAnnotations(triplet.b.getAnnotations());
            if (relationshipType != null) {
                return relationshipType;
            }
        }

        if (triplet.c != null) {
            relationshipType = this.relationshipTypeFromAnnotations(triplet.c.getAnnotations());
            if (relationshipType != null) {
                return relationshipType;
            }
        }

        if (triplet.a != null) {
            relationshipType = this.relationshipTypeFromField(triplet.a);
            if (relationshipType != null) {
                return relationshipType;
            }
        }

        if (triplet.b != null) {
            relationshipType = this.relationshipTypeFromGetter(triplet.b);
            if (relationshipType != null) {
                return relationshipType;
            }
        }

        if (triplet.b != null) {
            relationshipType = this.relationshipTypeFromSetter(triplet.c);
            if (relationshipType != null) {
                return relationshipType;
            }
        }

        return null;
    }

    /**
     * Returns relationship type from field.
     * @param field
     * @return The relationship type.
     */
    protected RelationshipType relationshipTypeFromField(Field field) {
        Class<?> target = field.getType();

        if (Collection.class.isAssignableFrom(target)) {
            return RelationshipType.ToMany;
        }
        return RelationshipType.ToOne;
    }

    /**
     * Returns relationship type from getter method.
     * @param getter
     * @return The relationship type.
     */
    protected RelationshipType relationshipTypeFromGetter(Method getter) {
        Class<?> target = getter.getReturnType();

        if (Collection.class.isAssignableFrom(target)) {
            return RelationshipType.ToMany;
        }
        return RelationshipType.ToOne;
    }

    /**
     * Returns relationship type from setter method.
     * @param setter
     * @return The relationship type.
     */
    protected RelationshipType relationshipTypeFromSetter(Method setter) {
        Class<?> target = setter.getParameterTypes()[0];

        if (Collection.class.isAssignableFrom(target)) {
            return RelationshipType.ToMany;
        }
        return RelationshipType.ToOne;
    }

    /**
     * Looks for JPA relationship annotation to find relationship type.
     * @param annotations
     * @return The relationship type.
     */
    protected RelationshipType relationshipTypeFromAnnotations(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(OneToOne.class)) {
                OneToOne oneToOne = (OneToOne)annotation;
                return RelationshipType.ToOne;

            } else if (annotation.annotationType().equals(OneToMany.class)) {
                OneToMany oneToMany = (OneToMany)annotation;
                return RelationshipType.ToMany;

            }  else if (annotation.annotationType().equals(ManyToOne.class)) {
                ManyToOne manyToOne = (ManyToOne)annotation;
                return RelationshipType.ToOne;

            }  else if (annotation.annotationType().equals(ManyToMany.class)) {
                ManyToMany manyToMany = (ManyToMany)annotation;
                return RelationshipType.ToMany;
            }
        }
        return null;
    }

    /**
     * Looks for target entity from triplet of field, getter and setter.
     * @param triplet
     * @param entity The entity of the property.
     * @param entities The known entities in the model.
     * @return The target entity or null if not found.
     */
    protected Entity targetEntity(Triplet<Field, Method, Method> triplet, Entity entity, Set<Entity> entities) {
        Class<?> target = this.targetClass(triplet);

        for (Entity e : entities) {
            if (e.getEntityClass().equals(target)) {
                return e;
            }
        }
        return null;
    }

    /**
     * Returns target class from triplet of field, getter and setter.
     * @param triplet
     * @return The target class.
     */
    protected Class<?> targetClass(Triplet<Field, Method, Method> triplet) {
        Class<?> target = null;

        if (triplet.a != null) {
            target = this.targetClassFromField(triplet.a);
            if (target == null) {
                target = this.targetClassFromAnnotations(triplet.a.getAnnotations());
            }

        } else if (triplet.b != null) {
            target = this.targetClassFromGetter(triplet.b);
            if (target == null) {
                target = this.targetClassFromAnnotations(triplet.b.getAnnotations());
            }

        } else if (triplet.c != null) {
            target = this.targetClassFromSetter(triplet.c);
            if (target == null) {
                target = this.targetClassFromAnnotations(triplet.c.getAnnotations());
            }
        }
        return target;
    }

    /**
     * Returns target class from field.
     * @param field
     * @return The target class.
     */
    protected Class<?> targetClassFromField(Field field) {
        Class<?> target = field.getType();

        if (Collection.class.isAssignableFrom(target)) {
            Type type = field.getGenericType();

            if (type instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType)type;
                Type[] arr = pType.getActualTypeArguments();
                target = (Class<?>)arr[0];
            }
        }
        return target;
    }

    /**
     * Returns target class from getter method.
     * @param getter
     * @return The target class.
     */
    protected Class<?> targetClassFromGetter(Method getter) {
        Class<?> target = getter.getReturnType();

        if (Collection.class.isAssignableFrom(target)) {
            Type type = getter.getGenericReturnType();

            if (type instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType)type;
                Type[] arr = pType.getActualTypeArguments();
                target = (Class<?>)arr[0];
            }
        }
        return target;
    }

    /**
     * Returns target class from setter method.
     * @param setter
     * @return The target class.
     */
    protected Class<?> targetClassFromSetter(Method setter) {
        Class<?> target = setter.getParameterTypes()[0];

        if (Collection.class.isAssignableFrom(target)) {
            Type type = setter.getGenericParameterTypes()[0];

            if (type instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType)type;
                Type[] arr = pType.getActualTypeArguments();
                target = (Class<?>)arr[0];
            }
        }
        return target;
    }

    /**
     * Looks for JPA relationship annotation to find target class.
     * @param annotations
     * @return The target class.
     */
    protected Class<?> targetClassFromAnnotations(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(OneToOne.class)) {
                OneToOne oneToOne = (OneToOne)annotation;
                return oneToOne.targetEntity();

            } else if (annotation.annotationType().equals(OneToMany.class)) {
                OneToMany oneToMany = (OneToMany)annotation;
                return oneToMany.targetEntity();

            }  else if (annotation.annotationType().equals(ManyToOne.class)) {
                ManyToOne manyToOne = (ManyToOne)annotation;
                return manyToOne.targetEntity();

            }  else if (annotation.annotationType().equals(ManyToMany.class)) {
                ManyToMany manyToMany = (ManyToMany)annotation;
                return manyToMany.targetEntity();
            }
        }
        return null;
    }

    protected Type attributeType(Triplet<Field, Method, Method> triplet, Entity entity) {
        Type type = null;

        if (triplet.a != null) {
            type = this.attributeTypeFromField(triplet.a, entity);
            if (type != null) {
                return type;
            }

        } else if (triplet.b != null) {
            type = this.attributeTypeFromGetter(triplet.b, entity);
            if (type != null) {
                return type;
            }

        } else if (triplet.c != null) {
            type = this.attributeTypeFromSetter(triplet.c, entity);
            if (type != null) {
                return type;
            }
        }
        return type;
    }

    protected Type attributeTypeFromField(Field field, Entity entity) {
        return field.getGenericType();
    }

    protected Type attributeTypeFromGetter(Method getter, Entity entity) {
        return getter.getGenericReturnType();
    }

    protected Type attributeTypeFromSetter(Method setter, Entity entity) {
        return setter.getGenericParameterTypes()[0];
    }

    /**
     * Determines if the triplet of the entity is primary attribute.
     *
     * Looks up for the JPA's Id annotation or matches property name "id".
     *
     * @param triplet
     * @param entity The entity.
     * @return True if triplet is primary.
     */
    protected boolean isPrimary(Triplet<Field, Method, Method> triplet, Entity entity) {
        Field field = triplet.a;
        Method getter = triplet.b;
        Method setter = triplet.c;

        Annotation annotation = (field != null) ? field.getAnnotation(Id.class) : null;
        if (annotation == null) {
            annotation = (getter != null) ? getter.getAnnotation(Id.class) : null;

            if (annotation == null) {
                annotation = (setter != null) ? setter.getAnnotation(Id.class) : null;
            }
        }

        if (annotation != null) {
            return true;
        }

        String name = this.propertyName(triplet, entity);
        return name.equalsIgnoreCase("id");
    }

    /**
     * Makes first letter of the string lower cased.
     * @param string
     * @return The first letter lower cased string.
     */
    protected String toFirstLetterLowerCase(String string) {
        return string.substring(0, 1).toLowerCase() + string.substring(1);
    }

}
