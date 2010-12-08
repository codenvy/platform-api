/*
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.vfs;

/**
 * Version identifier.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class VersionId
{
   private final String versionId;

   /**
    * @param versionId string representation of version id
    * @throw IllegalArgumentException if <code>versionId == null</code>
    */
   public VersionId(String versionId)
   {
      this.versionId = versionId;
   }

   /**
    * @return version's identifier as string
    */
   public String getVersionId()
   {
      return versionId;
   }
   
   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if ((obj == null) || (getClass() != obj.getClass()))
         return false;
      VersionId other = (VersionId)obj;
      return versionId.equals(other.versionId);
   }

   /**
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
   {
      int hash = 9;
      hash = hash * 31 + versionId.hashCode();
      return hash;
   }

   /**
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return "[VersionId: " + versionId + "]";
   }
}
