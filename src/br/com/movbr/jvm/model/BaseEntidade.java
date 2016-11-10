package br.com.movbr.jvm.model;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public abstract class BaseEntidade<ID extends Serializable> implements Serializable {
	private static final long serialVersionUID = 1L;

	@Transient
	public abstract ID getId();

	// contador para impedir que o numero se repita
	private static Long hashCounter = 0L;
	// hashid do objeto
	private Long hashId;
	// controla selecao
	private Boolean selecionado;

	public BaseEntidade() {
		synchronized (this) {
			BaseEntidade.hashCounter++;
			this.hashId = hashCounter;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if (getId() == null) {
			return this.hashId.hashCode();
		}
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		Object objInitialized = HibernateUtils.initializeAndUnproxy(obj);
		if (!getClass().equals(objInitialized.getClass()))
			return false;
		BaseEntidade<ID> other = (BaseEntidade<ID>) objInitialized;

		if (getClass() != other.getClass())
			return false;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			} else {
				// hashid identifica um objeto unicamente
				// avalia apenas se os id`s estiverem nulos
				if (hashId != null && other.getHashId() != null && hashId.equals(other.getHashId())) {
					return true;
				} else {
					return false;
				}
			}
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

	public String toString() {
		return getId() == null ? null : getId().toString();
	}

	@Transient
	public Long getHashId() {
		return hashId;
	}

	public void setHashId(Long hashId) {
		this.hashId = hashId;
	}

	@Transient
	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}
}