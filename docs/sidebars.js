/** @type {import('@docusaurus/plugin-content-docs').SidebarsConfig} */
const sidebars = {
  docs: [
    'index',
    {
      type: 'category',
      label: 'Getting Started',
      collapsible: false,
      items: [
        'getting-started/overview',
        'getting-started/core-entities',
        'getting-started/quickstart',
        'getting-started/running',
      ],
    },
    {
      type: 'category',
      label: 'Architecture',
      items: [
        'architecture/modules',
        'architecture/configuration',
        'architecture/data-model',
      ],
    },
    {
      type: 'category',
      label: 'API',
      items: [
        'api/overview',
        'api/thunder-api-contracts',
      ],
    },
    {
      type: 'category',
      label: 'Admin',
      items: [
        'admin/overview',
        'api/admin-contracts',
      ],
    },
    {
      type: 'category',
      label: 'Operations',
      items: [
        'operations/docker',
        'operations/testing',
        'operations/ci-cd',
        'operations/releases',
      ],
    },
  ],
};

module.exports = sidebars;



