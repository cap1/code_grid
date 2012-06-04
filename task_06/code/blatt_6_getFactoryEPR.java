private EndpointReferenceType getFactoryEPR (String contact, String factoryType)
throws Exception
{
    URL factoryUrl = ManagedJobFactoryClientHelper.getServiceURL(contact).getURL();
    logger.debug("Factory Url: " + factoryUrl);
    return ManagedJobFactoryClientHelper.getFactoryEndpoint(factoryUrl, factoryType);
}
