
package cz.cvut.fel.adaptiverestfulapi.example;

import cz.cvut.fel.adaptiverestfulapi.core.HttpContext;
import cz.cvut.fel.adaptiverestfulapi.data.GetHandler;
import cz.cvut.fel.adaptiverestfulapi.meta.configuration.Configuration;
import cz.cvut.fel.adaptiverestfulapi.meta.model.Attribute;
import cz.cvut.fel.adaptiverestfulapi.meta.model.Entity;
import cz.cvut.fel.adaptiverestfulapi.meta.model.Relationship;


public class DescriptionDataHandler implements GetHandler {

    @Override
    public Object get(Entity entity, HttpContext context, Configuration configuration) {
        StringBuilder sb = new StringBuilder();

        sb.append("Entity:\n");
        sb.append(entity.getName());
        sb.append("\n\n");

        sb.append("Attributes:\n");
        for (Attribute attr : entity.getAttributes().values()) {
            sb.append(attr.getName());
            sb.append("\n");
        }
        sb.append("\n\n");

        sb.append("Relationships:\n");
        for (Relationship rel : entity.getRelationships().values()) {
            sb.append(rel.getName());
            sb.append("\n");
        }

        return sb.toString();
    }

}
