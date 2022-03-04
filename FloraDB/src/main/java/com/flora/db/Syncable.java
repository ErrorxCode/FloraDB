package com.flora.db;

import java.io.Serializable;

/**
 * A simple meta-info interface that makes an object serializable and cloneable. Every POJO must implement this
 */
public interface Syncable extends Serializable, Cloneable {

}
