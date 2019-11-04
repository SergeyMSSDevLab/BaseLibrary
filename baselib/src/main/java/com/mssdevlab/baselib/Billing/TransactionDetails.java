/*
  Copyright 2014 AnjLab

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package com.mssdevlab.baselib.Billing;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.Locale;

public class TransactionDetails implements Parcelable
{

	private String getProductId() {
		return purchaseInfo.purchaseData.productId;
	}

	private String getOrderId() {
		return purchaseInfo.purchaseData.orderId;
	}

	private String getPurchaseToken(){
		return purchaseInfo.purchaseData.purchaseToken;
	}

	private Date getPurchaseTime(){
		return purchaseInfo.purchaseData.purchaseTime;
	}

	final PurchaseInfo purchaseInfo;

	TransactionDetails(PurchaseInfo info)
	{
		purchaseInfo = info;
	}

	@NonNull
	@Override
	public String toString()
	{
		return String.format(Locale.US, "%s purchased at %s(%s). Token: %s, Signature: %s",
							 getProductId(),
							 getPurchaseTime(),
							 getOrderId(),
							 getPurchaseToken(),
							 purchaseInfo.signature);
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		TransactionDetails details = (TransactionDetails) o;

		return !(getOrderId() != null ? !getOrderId().equals(details.getOrderId()) : details.getOrderId() != null);
	}

	@Override
	public int hashCode()
	{
		return getOrderId() != null ? getOrderId().hashCode() : 0;
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeParcelable(this.purchaseInfo, flags);
	}

	private TransactionDetails(Parcel in)
	{
		this.purchaseInfo = in.readParcelable(PurchaseInfo.class.getClassLoader());
	}

	public static final Parcelable.Creator<TransactionDetails> CREATOR =
			new Parcelable.Creator<TransactionDetails>()
			{
				public TransactionDetails createFromParcel(Parcel source)
				{
					return new TransactionDetails(source);
				}

				public TransactionDetails[] newArray(int size)
				{
					return new TransactionDetails[size];
				}
			};
}
