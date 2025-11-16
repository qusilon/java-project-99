package hexlet.code.specification;

import hexlet.code.model.Task;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TaskSpecification {
    public Specification<Task> build(
            String titleCont,
            Long assigneeId,
            String status,
            Long labelId
    ) {
        return withTitleCont(titleCont)
                .and(withAssigneeId(assigneeId))
                .and(withStatus(status))
                .and(withLabelId(labelId));
    }

    private Specification<Task> withTitleCont(String titleCont) {
        return (root, query, cb) ->
                titleCont == null
                        ? cb.conjunction()
                        : cb.like(
                        cb.lower(root.get("name")),
                        "%" + titleCont.toLowerCase() + "%"
                );
    }

    private Specification<Task> withAssigneeId(Long assigneeId) {
        return (root, query, cb) ->
                assigneeId == null
                        ? cb.conjunction()
                        : cb.equal(root.get("assignee").get("id"), assigneeId);
    }

    private Specification<Task> withStatus(String status) {
        return (root, query, cb) ->
                status == null
                        ? cb.conjunction()
                        : cb.equal(root.get("taskStatus").get("slug"), status);
    }

    private Specification<Task> withLabelId(Long labelId) {
        return (root, query, cb) -> {
            if (labelId == null) {
                return cb.conjunction();
            }
            var join = root.join("labels");
            return cb.equal(join.get("id"), labelId);
        };
    }
}
