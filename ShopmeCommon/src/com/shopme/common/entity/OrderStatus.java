package com.shopme.common.entity;

public enum OrderStatus {
	NEW {
		public String getDescription() {
			return "Order was placed by customer";
		}
	},
	
	CANCELLED {
		public String getDescription() {
			return "Order was cancelled";
		}		
	}, 
	
	PROCESSING {
		public String getDescription() {
			return "Order is being proccessed";
		}		
	},
	
	PACKAGED {
		public String getDescription() {
			return "Products are packed for shipping";
		}		
	},
	
	PICKED {
		public String getDescription() {
			return "Shipper picked the package";
		}		
	},
	
	SHIPPING {
		public String getDescription() {
			return "Package is on the way to deliver";
		}		
	},
	
	DELIVERED {
		public String getDescription() {
			return "Package was delivered";
		}		
	},
	
	RETURNED {
		public String getDescription() {
			return "Package was returned";
		}		
	},
	
	PAID {
		public String getDescription() {
			return "Customer has paid this order";
		}		
	},
	
	REFUNDED {
		public String getDescription() {
			return "Order was refunded to customer";
		}		
	};
	
	public String getDescription() {
		return "";
	}
}
