package br.com.aelkz.service;

import br.com.aelkz.exception.EntityException;
import br.com.aelkz.model.User;
import br.com.aelkz.repository.UserRepository;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Set;
import java.util.logging.Logger;

@ManagedBean
@RequestScoped
public class UserService implements GenericService<User>, Serializable {

    private final Logger LOGGER = Logger.getLogger(UserService.class.getName());

    private User item;
    private Boolean preUpdateOperation;

    public UserService() {}

    @Inject
    private UserRepository repository;

    @PostConstruct
    public void init() {
        item = new User();
        setPreUpdateOperation(false);
    }

    @Override
    public void save() throws EntityException {
        try {
            if (validate(item)) {
                repository.save(item);
                if(item.getResume() != null) {
                    FacesMessage message = new FacesMessage("Succesfull", item.getResume().getFileName() + " is uploaded.");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                }
                FacesMessage message = new FacesMessage("New user added",item.getLastName()+","+item.getFirstName()+" added to database.");
                FacesContext.getCurrentInstance().addMessage(null, message);
                item = new User();
            }
        } catch (EntityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void preUpdate(User entity) throws EntityException {
        this.item = entity;
        setPreUpdateOperation(true);
    }

    @Override
    public void update() throws EntityException {
        User updated = repository.findBy(item.getId());
        updated.setFirstName(item.getFirstName());
        updated.setLastName(item.getLastName());
        updated.setEmail(item.getEmail());
        updated.setCellphone(item.getCellphone());

        repository.update(updated);
        this.item = new User();
        setPreUpdateOperation(false);
    }

    @Override
    public void preRemove(User entity) throws EntityException {

    }

    @Override
    public void remove(User entity) throws EntityException {
        repository.remove(entity);
    }

    @Override
    public Set<User> getAll() throws EntityException {
        Set<User> users = repository.getStorage();
        return users;
    }

    @Override
    public Boolean validate(User entity) throws EntityException {
        return entity != null;
    }

    @Override
    public void cancel() throws EntityException {
        setPreUpdateOperation(false);
        this.item = new User();
    }

    public User getItem() {
        return item;
    }

    public void setItem(User item) {
        this.item = item;
    }

    public Boolean getPreUpdateOperation() {
        return preUpdateOperation;
    }

    public void setPreUpdateOperation(Boolean preUpdateOperation) {
        this.preUpdateOperation = preUpdateOperation;
    }
}
