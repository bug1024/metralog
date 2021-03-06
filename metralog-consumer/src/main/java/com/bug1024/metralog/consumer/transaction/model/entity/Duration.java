package com.bug1024.metralog.consumer.transaction.model.entity;

import com.bug1024.metralog.consumer.transaction.model.BaseEntity;
import com.bug1024.metralog.consumer.transaction.model.IVisitor;

import static com.bug1024.metralog.consumer.transaction.model.Constants.ATTR_VALUE;
import static com.bug1024.metralog.consumer.transaction.model.Constants.ENTITY_DURATION;

public class Duration extends BaseEntity<Duration> {
   private int m_value;

   private int m_count;

   public Duration() {
   }

   public Duration(int value) {
      m_value = value;
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitDuration(this);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Duration) {
         Duration _o = (Duration) obj;

         if (getValue() != _o.getValue()) {
            return false;
         }

         return true;
      }

      return false;
   }

   public int getCount() {
      return m_count;
   }

   public int getValue() {
      return m_value;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      hash = hash * 31 + m_value;

      return hash;
   }

   public Duration incCount() {
      m_count++;
      return this;
   }

   public Duration incCount(int count) {
      m_count += count;
      return this;
   }

   @Override
   public void mergeAttributes(Duration other) {
      assertAttributeEquals(other, ENTITY_DURATION, ATTR_VALUE, m_value, other.getValue());

      m_count = other.getCount();
   }

   public Duration setCount(int count) {
      m_count = count;
      return this;
   }

   public Duration setValue(int value) {
      m_value = value;
      return this;
   }

}
