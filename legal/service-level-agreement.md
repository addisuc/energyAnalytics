# Service Level Agreement (SLA)

**Effective Date**: January 1, 2024  
**Version**: 1.0

## 1. Service Availability

### 1.1 Uptime Commitment
- **Enterprise**: 99.9% uptime (8.77 hours downtime/year)
- **Pro**: 99.5% uptime (43.8 hours downtime/year)
- **Basic**: 99.0% uptime (87.6 hours downtime/year)

### 1.2 Planned Maintenance
- Maximum 4 hours/month during off-peak hours
- 48-hour advance notice for maintenance windows
- Emergency maintenance with 2-hour notice when possible

## 2. Performance Standards

### 2.1 API Response Times
- **Enterprise**: <200ms average, <500ms 95th percentile
- **Pro**: <500ms average, <1s 95th percentile
- **Basic**: <1s average, <2s 95th percentile

### 2.2 Data Freshness
- Real-time data: <5 minutes delay
- Historical data: Updated within 24 hours
- Weather data: <15 minutes from source

## 3. Support Response Times

### 3.1 Enterprise Support
- **Critical**: 1 hour response, 4 hours resolution
- **High**: 4 hours response, 24 hours resolution
- **Medium**: 24 hours response, 72 hours resolution
- **Low**: 72 hours response, 1 week resolution

### 3.2 Pro Support
- **Critical**: 4 hours response, 24 hours resolution
- **High**: 24 hours response, 72 hours resolution
- **Medium**: 72 hours response, 1 week resolution

### 3.3 Basic Support
- **All Issues**: 72 hours response, best effort resolution

## 4. Service Credits

### 4.1 Availability Credits
- 99.0-99.8%: 10% monthly credit
- 95.0-98.9%: 25% monthly credit
- <95.0%: 50% monthly credit

### 4.2 Performance Credits
- API response >2x SLA: 5% monthly credit
- Data delays >1 hour: 10% monthly credit

## 5. Exclusions

SLA does not apply to:
- Third-party service outages
- Customer configuration errors
- DDoS attacks or security incidents
- Force majeure events

## 6. Monitoring & Reporting

- Real-time status page: status.energyanalytics.com
- Monthly SLA reports for Enterprise customers
- Incident post-mortems within 72 hours